#!/usr/bin/env bash
unset SCRIPT_ROOT
readonly SCRIPT_ROOT=$(
  unset CDPATH
  script_root=$(dirname "${BASH_SOURCE}")
  cd "${script_root}"
  pwd
)
if [ -d "${SCRIPT_ROOT}/submodules/flogo-cicd" ]; then
  rm -rf ${SCRIPT_ROOT}/submodules/flogo-cicd
  git submodule update --init --remote --recursive
  source ${SCRIPT_ROOT}/submodules/flogo-cicd/scripts/init.sh
  # Build flogo/flow-service docker image
  pushd ${SCRIPT_ROOT}
  cp ${SCRIPT_ROOT}/submodules/flogo-cicd/docker/flow-service/Dockerfile ./Dockerfile-flow-service
  docker::build_and_push flogo/flow-service Dockerfile-flow-service 
  popd
fi
