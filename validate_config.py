from __future__ import annotations

import sys
from pathlib import Path

CONFIG_PATH = (
    Path(__file__).resolve().parent / "src" / "main" / "resources" / "config.yml"
)


def main() -> int:
    if not CONFIG_PATH.exists():
        print(f"Error: config file not found: {CONFIG_PATH}")
        return 1

    try:
        import yaml
    except ImportError:
        print(
            "Error: PyYAML is not installed. Install it with: python -m pip install pyyaml"
        )
        return 1

    try:
        text = CONFIG_PATH.read_text(encoding="utf-8")
    except Exception as exc:
        print(f"Error reading file {CONFIG_PATH}: {exc}")
        return 1

    try:
        yaml.safe_load(text)
    except yaml.YAMLError as exc:
        print(f"YAML parse error in {CONFIG_PATH}:")
        print(exc)
        return 1

    print(f"OK: {CONFIG_PATH} is valid YAML")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
