from __future__ import annotations

import os
import shutil
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path

# Configuration
JAVA_HOME = r"C:\Program Files\Java\jdk-21.0.11"
BUILD_COMMAND = rf"$env:JAVA_HOME='{JAVA_HOME}'; mvn clean package"
PLUGINS_DIR = Path(r"D:\Minecraft Server\plugins")
RESET_CONFIG = False
SERVER_ASSISTANT_CONFIG_DIR_NAME = "ServerAssistant"
BUILD_TIMEOUT_SECONDS = 300
POLL_INTERVAL_SECONDS = 1


def parse_pom(root: Path) -> tuple[str, str]:
    pom_path = root / "pom.xml"
    tree = ET.parse(pom_path)
    ns = {"m": "http://maven.apache.org/POM/4.0.0"}
    artifact_id = tree.findtext("m:artifactId", namespaces=ns)
    version = tree.findtext("m:version", namespaces=ns)
    if not artifact_id or not version:
        raise RuntimeError("Unable to parse artifactId/version from pom.xml")
    return artifact_id.strip(), version.strip()


def find_generated_jar(target_dir: Path, artifact_id: str, version: str) -> Path:
    expected_name = f"{artifact_id}-{version}.jar"
    jars = sorted(target_dir.glob("*.jar"))
    if not jars:
        raise FileNotFoundError(f"No jar files found in {target_dir}")

    exact_matches = [jar for jar in jars if jar.name == expected_name]
    if exact_matches:
        return exact_matches[0]

    non_original = [jar for jar in jars if not jar.name.startswith("original-")]
    if non_original:
        return non_original[0]

    return jars[0]


def wait_for_jar(
    target_dir: Path, artifact_id: str, version: str, timeout: int
) -> Path:
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        try:
            jar = find_generated_jar(target_dir, artifact_id, version)
            if jar.exists() and jar.stat().st_size > 0:
                return jar
        except FileNotFoundError:
            pass
        time.sleep(POLL_INTERVAL_SECONDS)
    raise TimeoutError(f"Timed out waiting for generated jar in {target_dir}")


def find_plugin_jar(plugins_dir: Path, target_jar_name: str, artifact_id: str) -> Path:
    exact_match = plugins_dir / target_jar_name
    if exact_match.exists():
        return exact_match

    candidates = [
        jar
        for jar in plugins_dir.glob("*.jar")
        if jar.stem == artifact_id
        or artifact_id in jar.name
        or jar.name.startswith("ServerAssistant")
    ]
    if not candidates:
        raise FileNotFoundError(
            f"No replacement jar found in {plugins_dir} matching {artifact_id} or {target_jar_name}"
        )
    return sorted(candidates, key=lambda path: path.name)[0]


def remove_config_folder(plugins_dir: Path, config_dir_name: str) -> None:
    config_path = plugins_dir / config_dir_name
    if config_path.exists() and config_path.is_dir():
        shutil.rmtree(config_path)
        print(f"Removed config folder: {config_path}")
    else:
        print(f"Config folder not found, skipping: {config_path}")


def main() -> None:
    root = Path(__file__).resolve().parent
    target_dir = root / "target"
    if not target_dir.exists():
        raise FileNotFoundError(f"Target directory not found: {target_dir}")
    if not PLUGINS_DIR.exists():
        raise FileNotFoundError(f"Plugins directory not found: {PLUGINS_DIR}")

    artifact_id, version = parse_pom(root)
    print(f"Building project '{artifact_id}' version '{version}'")

    completed = subprocess.run(
        ["powershell", "-NoProfile", "-Command", BUILD_COMMAND],
        cwd=root,
        check=True,
        capture_output=True,
        text=True,
    )
    print(completed.stdout)
    if completed.stderr:
        print(completed.stderr)

    print("Waiting for generated jar...")
    generated_jar = wait_for_jar(
        target_dir, artifact_id, version, BUILD_TIMEOUT_SECONDS
    )
    print(f"Found generated jar: {generated_jar}")

    plugin_jar = find_plugin_jar(PLUGINS_DIR, generated_jar.name, artifact_id)
    print(f"Found plugin jar to replace: {plugin_jar}")

    shutil.copy2(generated_jar, plugin_jar)
    print(f"Replaced plugin jar with built jar: {plugin_jar}")

    if RESET_CONFIG:
        remove_config_folder(PLUGINS_DIR, SERVER_ASSISTANT_CONFIG_DIR_NAME)

    print("Build and replace completed successfully.")


if __name__ == "__main__":
    main()
