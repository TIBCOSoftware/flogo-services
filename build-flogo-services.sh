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

  cp ${SCRIPT_ROOT}/submodules/flogo-cicd/docker/state-service/Dockerfile ./Dockerfile-flow-state-service
  docker::build_and_push flogo/state-service Dockerfile-flow-state-service 

  {
    echo "#!/bin/bash"
    echo "script_root=\$(dirname \"\${BASH_SOURCE}\")"
    [ -n "${BUILD_RELEASE_TAG}" ] && echo "export BUILD_RELEASE_TAG=${BUILD_RELEASE_TAG}"
    [ -n "${DOCKER_REGISTRY}" ] && echo "export DOCKER_REGISTRY=${DOCKER_REGISTRY}"
    echo "docker-compose -f \${script_root}/docker-compose.yml up"
    echo "docker-compose rm -f"
  } > ${SCRIPT_ROOT}/docker-compose-start.sh && \
        chmod +x ${SCRIPT_ROOT}/docker-compose-start.sh

  popd
fi



