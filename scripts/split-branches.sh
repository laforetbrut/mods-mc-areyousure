#!/usr/bin/env bash
# Regenerates one branch per target from main.
# Each target branch gets the loader project at its root, the shared code of its Minecraft
# version in common/, and the repository documentation. History is kept: every run adds one
# commit on top of the existing target branch.
#
# Usage: scripts/split-branches.sh [version]
#   version  optional, also creates the annotated tag v<version>-<target> on each branch
#
# Author: vyrriox
set -euo pipefail

TARGETS=(neoforge-1.21.1 forge-1.21.1 neoforge-26.1.2 forge-26.1.2)
SOURCE=main
VERSION="${1:-}"

cd "$(git rev-parse --show-toplevel)"
source_sha=$(git rev-parse --verify "$SOURCE")
index_file=$(mktemp)
trap 'rm -f "$index_file"' EXIT

is_target_dir() {
    local path=$1 t
    for t in "${TARGETS[@]}"; do
        [[ $path == "$t/"* ]] && return 0
    done
    return 1
}

for target in "${TARGETS[@]}"; do
    mc=${target#*-}
    rm -f "$index_file"
    export GIT_INDEX_FILE=$index_file
    git read-tree --empty

    while IFS=$'\t' read -r meta path; do
        read -r mode _ sha <<<"$meta"
        if [[ $path == "$target/"* ]]; then
            dest=${path#"$target/"}
        elif [[ $path == "common/$mc/"* ]]; then
            dest=common/${path#"common/$mc/"}
        elif [[ $path == common/* || $path == scripts/* ]] || is_target_dir "$path"; then
            continue
        else
            dest=$path
        fi
        git update-index --add --cacheinfo "$mode,$sha,$dest"
    done < <(git ls-tree -r "$source_sha")

    tree=$(git write-tree)
    unset GIT_INDEX_FILE

    parent_args=()
    if parent=$(git rev-parse -q --verify "refs/heads/$target"); then
        if [[ $(git rev-parse "$parent^{tree}") == "$tree" ]]; then
            echo "$target: up to date"
        else
            parent_args=(-p "$parent")
            commit=$(git commit-tree "$tree" "${parent_args[@]}" -m "chore: regenerate $target from $SOURCE $(git rev-parse --short "$source_sha")")
            git update-ref "refs/heads/$target" "$commit"
            echo "$target: $commit"
        fi
    else
        commit=$(git commit-tree "$tree" -m "chore: generate $target from $SOURCE $(git rev-parse --short "$source_sha")")
        git update-ref "refs/heads/$target" "$commit"
        echo "$target: $commit (new)"
    fi

    if [[ -n $VERSION ]]; then
        git tag -a "v$VERSION-$target" "refs/heads/$target" -m "Are You Sure? $VERSION for $target"
    fi
done
