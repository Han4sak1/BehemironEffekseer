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
