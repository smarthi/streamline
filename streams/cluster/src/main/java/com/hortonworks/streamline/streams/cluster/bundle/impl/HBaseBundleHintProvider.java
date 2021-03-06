/**
  * Copyright 2017 Hortonworks.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at

  *   http://www.apache.org/licenses/LICENSE-2.0

  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
 **/
package com.hortonworks.streamline.streams.cluster.bundle.impl;

import com.hortonworks.streamline.streams.catalog.Cluster;
import com.hortonworks.streamline.streams.catalog.exception.ServiceConfigurationNotFoundException;
import com.hortonworks.streamline.streams.catalog.exception.ServiceNotFoundException;
import com.hortonworks.streamline.streams.cluster.Constants;
import com.hortonworks.streamline.streams.cluster.bundle.AbstractBundleHintProvider;
import com.hortonworks.streamline.streams.cluster.service.metadata.HBaseMetadataService;

import javax.security.auth.Subject;
import javax.ws.rs.core.SecurityContext;
import java.util.HashMap;
import java.util.Map;

public class HBaseBundleHintProvider extends AbstractBundleHintProvider {
    public static final String FIELD_NAME_TABLE = "table";

    @Override
    public Map<String, Object> getHintsOnCluster(Cluster cluster, SecurityContext securityContext, Subject subject) {
        Map<String, Object> hintMap = new HashMap<>();
        try (HBaseMetadataService hBaseMetadataService = HBaseMetadataService.newInstance(environmentService, cluster.getId(), securityContext, subject)) {
            hintMap.put(FIELD_NAME_TABLE, hBaseMetadataService.getHBaseTables().getTables());
        } catch (ServiceNotFoundException e) {
            // we access it from mapping information so shouldn't be here
            throw new IllegalStateException("Service " + Constants.HBase.SERVICE_NAME + " in cluster " + cluster.getName() +
                    " not found but mapping information exists.");
        } catch (ServiceConfigurationNotFoundException e) {
            // there's HBASE service but not enough configuration info.
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return hintMap;
    }

    @Override
    public String getServiceName() {
        return Constants.HBase.SERVICE_NAME;
    }
}
