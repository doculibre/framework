package com.vaadin.server;

import java.io.Serializable;
import java.util.Properties;

import com.vaadin.LegacyApplication;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.UI;

/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

class ServletPortletHelper implements Serializable {
    public static final String UPLOAD_URL_PREFIX = "APP/UPLOAD/";
    /**
     * The default SystemMessages (read-only).
     */
    static final SystemMessages DEFAULT_SYSTEM_MESSAGES = new SystemMessages();

    public static class ApplicationClassException extends Exception {

        public ApplicationClassException(String message, Throwable cause) {
            super(message, cause);
        }

        public ApplicationClassException(String message) {
            super(message);
        }
    }

    static Class<? extends LegacyApplication> getLegacyApplicationClass(
            VaadinService vaadinService) throws ApplicationClassException {
        Properties initParameters = vaadinService.getDeploymentConfiguration()
                .getInitParameters();
        String applicationParameter = initParameters.getProperty("application");
        ClassLoader classLoader = vaadinService.getClassLoader();

        if (applicationParameter == null) {
            throw new ApplicationClassException(
                    "No \"application\" init parameter found");
        }

        try {
            return classLoader.loadClass(applicationParameter).asSubclass(
                    LegacyApplication.class);
        } catch (final ClassNotFoundException e) {
            throw new ApplicationClassException(
                    "Failed to load application class: " + applicationParameter,
                    e);
        }
    }

    private static void verifyUIClass(String className, ClassLoader classLoader)
            throws ApplicationClassException {
        if (className == null) {
            throw new ApplicationClassException(VaadinSession.UI_PARAMETER
                    + " init parameter not defined");
        }

        // Check that the UI layout class can be found
        try {
            Class<?> uiClass = classLoader.loadClass(className);
            if (!UI.class.isAssignableFrom(uiClass)) {
                throw new ApplicationClassException(className
                        + " does not implement UI");
            }
            // Try finding a default constructor, else throw exception
            uiClass.getConstructor();
        } catch (ClassNotFoundException e) {
            throw new ApplicationClassException(className
                    + " could not be loaded", e);
        } catch (SecurityException e) {
            throw new ApplicationClassException("Could not access " + className
                    + " class", e);
        } catch (NoSuchMethodException e) {
            throw new ApplicationClassException(className
                    + " doesn't have a public no-args constructor");
        }
    }

    private static boolean hasPathPrefix(WrappedRequest request, String prefix) {
        String pathInfo = request.getRequestPathInfo();

        if (pathInfo == null) {
            return false;
        }

        if (!prefix.startsWith("/")) {
            prefix = '/' + prefix;
        }

        if (pathInfo.startsWith(prefix)) {
            return true;
        }

        return false;
    }

    public static boolean isFileUploadRequest(WrappedRequest request) {
        return hasPathPrefix(request, UPLOAD_URL_PREFIX);
    }

    public static boolean isConnectorResourceRequest(WrappedRequest request) {
        return hasPathPrefix(request,
                ApplicationConstants.CONNECTOR_RESOURCE_PREFIX + "/");
    }

    public static boolean isUIDLRequest(WrappedRequest request) {
        return hasPathPrefix(request, ApplicationConstants.UIDL_REQUEST_PATH);
    }

    public static boolean isApplicationResourceRequest(WrappedRequest request) {
        return hasPathPrefix(request, ApplicationConstants.APP_REQUEST_PATH);
    }

    public static boolean isHeartbeatRequest(WrappedRequest request) {
        return hasPathPrefix(request,
                ApplicationConstants.HEARTBEAT_REQUEST_PATH);
    }

    public static void initDefaultUIProvider(VaadinSession application,
            VaadinService vaadinService) throws ApplicationClassException {
        String uiProperty = vaadinService.getDeploymentConfiguration()
                .getInitParameters().getProperty(VaadinSession.UI_PARAMETER);
        if (uiProperty != null) {
            verifyUIClass(uiProperty, vaadinService.getClassLoader());
            application.addUIProvider(new DefaultUIProvider());
        }
    }

    public static void checkUiProviders(VaadinSession newApplication)
            throws ApplicationClassException {
        if (newApplication.getUIProviders().isEmpty()) {
            throw new ApplicationClassException(
                    "No UIProvider has been added to the application and there is no \""
                            + VaadinSession.UI_PARAMETER + "\" init parameter.");
        }
    }

}
