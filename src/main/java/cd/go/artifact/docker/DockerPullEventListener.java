/*
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.artifact.docker;

import io.fabric8.docker.dsl.EventListener;

import java.util.concurrent.CountDownLatch;

import static cd.go.artifact.docker.DockerArtifactPlugin.LOG;

public class DockerPullEventListener implements EventListener {
    private final CountDownLatch pushDone = new CountDownLatch(1);
    private DockerPullResponse dockerPullResponse;

    public DockerPullEventListener() {
        dockerPullResponse = new DockerPullResponse();
    }

    @Override
    public void onSuccess(String message) {
        LOG.info("Success:" + message);
        pushDone.countDown();
    }

    @Override
    public void onError(String message) {
        pushDone.countDown();
        dockerPullResponse.exception(new RuntimeException(message));
    }

    @Override
    public void onError(Throwable t) {
        pushDone.countDown();
        LOG.error("Failure: ", t);
        dockerPullResponse.exception(t);
    }

    @Override
    public void onEvent(String event) {
        LOG.info(String.format("Docker Pull: %s", event));
    }

    public DockerPullResponse await() throws InterruptedException {
        pushDone.await();
        return this.dockerPullResponse;
    }
}