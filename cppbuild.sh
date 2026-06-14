#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 2 ]]; then
  echo "usage: $0 <platform> <build-dir>"
  exit 1
fi

PLATFORM="$1"
BUILD_DIR="$2"
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
INSTALL_DIR="$ROOT_DIR/cppbuild/$PLATFORM"

file_size_bytes() {
  if stat --version >/dev/null 2>&1; then
    stat -c %s "$1"
  else
    stat -f %z "$1"
  fi
}

find_android_llvm_strip() {
  local ndk_root="${ANDROID_NDK:-${ANDROID_NDK_HOME:-}}"
  local candidate

  if [[ -z "$ndk_root" ]]; then
    return 1
  fi

  for candidate in \
    "$ndk_root/toolchains/llvm/prebuilt/linux-x86_64/bin/llvm-strip" \
    "$ndk_root/toolchains/llvm/prebuilt/darwin-x86_64/bin/llvm-strip" \
    "$ndk_root/toolchains/llvm/prebuilt/darwin-arm64/bin/llvm-strip" \
    "$ndk_root/toolchains/llvm/prebuilt/windows-x86_64/bin/llvm-strip.exe" \
    "$ndk_root/toolchains/llvm/prebuilt/windows-x86_64/bin/llvm-strip"
  do
    if [[ -x "$candidate" ]]; then
      printf '%s\n' "$candidate"
      return 0
    fi
  done

  candidate="$(find "$ndk_root/toolchains/llvm/prebuilt" -maxdepth 3 -type f \( -name 'llvm-strip' -o -name 'llvm-strip.exe' \) -print -quit 2>/dev/null || true)"
  if [[ -n "$candidate" ]]; then
    printf '%s\n' "$candidate"
    return 0
  fi

  return 1
}

strip_android_shared_libraries() {
  local strip_bin
  local file
  local before_size
  local after_size

  if ! strip_bin="$(find_android_llvm_strip)"; then
    echo "warning: Android NDK llvm-strip not found, skipping Android shared library stripping" >&2
    return 0
  fi

  while IFS= read -r -d '' file; do
    before_size="$(file_size_bytes "$file")"
    "$strip_bin" --strip-debug "$file"
    after_size="$(file_size_bytes "$file")"
    echo "Stripped Android shared library $(basename "$file"): ${before_size} -> ${after_size} bytes"
  done < <(find "$INSTALL_DIR/lib" -maxdepth 1 -type f -name '*.so' -print0)
}

mkdir -p "$INSTALL_DIR/include" "$INSTALL_DIR/lib" "$INSTALL_DIR/bin"

find "$ROOT_DIR/src/cpp/Core" -maxdepth 1 -type f -name 'BehemironEffekseer*.h' -exec cp {} "$INSTALL_DIR/include/" \;

case "$PLATFORM" in
  windows-x86_64)
    find "$BUILD_DIR" -type f \( -name '*.dll' -o -name '*.lib' \) -print0 | while IFS= read -r -d '' file; do
      case "$file" in
        *.dll) cp "$file" "$INSTALL_DIR/bin/" ;;
        *.lib) cp "$file" "$INSTALL_DIR/lib/" ;;
      esac
    done
    ;;
  *)
    find "$BUILD_DIR" -type f \( -name '*.so' -o -name '*.dylib' -o -name '*.a' \) -print0 | while IFS= read -r -d '' file; do
      case "$file" in
        *.a) cp "$file" "$INSTALL_DIR/lib/" ;;
        *.so|*.dylib) cp "$file" "$INSTALL_DIR/lib/" ;;
      esac
    done
    ;;
esac

if [[ "$PLATFORM" == "android-arm64" ]]; then
  strip_android_shared_libraries
fi
