/*
 *
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package org.wso2.carbon.appfactory.jenkins.extentions;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.maven.AbstractMavenBuild;
import hudson.maven.local_repo.LocalRepositoryLocator;
import hudson.maven.local_repo.LocalRepositoryLocatorDescriptor;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;

/**
 * Maven repository locator
 */
public class AFLocalRepositoryLocator extends LocalRepositoryLocator {

    public static final String PLACEHOLDER_JEN_HOME = "$JENKINS_HOME";
    public static final String PLACEHOLDER_TENANT_IDENTIFIER = "$TENANT_IDENTIFIER";
    public static final String DEFAULT_REPO_PATH_SUFFIX = ".repository";

    @DataBoundConstructor
    public AFLocalRepositoryLocator() {
    }

    @Override
    public FilePath locate(AbstractMavenBuild build) {
        //TODO add validation
        String jenkinsHome = EnvVars.masterEnvVars.get("JENKINS_HOME");
        String tenantGroup = build.getParent().getParent().getFullName();
        String tenantRepositoryDirPattern = getDescriptor().getTenantRepositoryDirPattern();
        String repoPath = jenkinsHome + File.separator + DEFAULT_REPO_PATH_SUFFIX;
        if (StringUtils.isNotEmpty(tenantRepositoryDirPattern)) {
            repoPath = tenantRepositoryDirPattern.replace(PLACEHOLDER_JEN_HOME, jenkinsHome).replace
                    (PLACEHOLDER_TENANT_IDENTIFIER, tenantGroup);

        }
        return new FilePath(new File(repoPath));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends LocalRepositoryLocatorDescriptor {
        public DescriptorImpl() {
            super();
            load();
        }

        private String tenantRepositoryDirPattern;

        public void setTenantRepositoryDirPattern(String tenantRepositoryDirPattern) {
            this.tenantRepositoryDirPattern = tenantRepositoryDirPattern;
        }

        public String getTenantRepositoryDirPattern() {
            return tenantRepositoryDirPattern;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            setTenantRepositoryDirPattern(formData.getString("tenantRepositoryDirPattern"));
            save();
            return super.configure(req, formData);
        }

        public String getDisplayName() {
            return "Local to the tenant workspace";
        }
    }
}