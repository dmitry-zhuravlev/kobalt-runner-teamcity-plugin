<%@ page import="static com.buildServer.kobalt.common.KobaltRunnerConstants.*" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="kobaltVersionSelectBean" class="com.buildServer.kobalt.server.KobaltVersionSelectBean"
             scope="request"/>

<l:settingsGroup title="Kobalt Parameters">
    <tr>
        <th><label for="ui.kobalt.runner.kobalt.tasks">Kobalt tasks: </label></th>
        <td><props:textProperty name="<%=KOBALT_TASKS%>" className="longField" maxlength="256"/>
            <span class="smallNote">Enter task names separated by spaces, leave blank to use the 'default' task.<br/>Example: 'myproject:clean myproject:build' or 'clean build'.</span>
        </td>
    </tr>
    <tr>
        <th><label for="ui.kobalt.runner.build.file.path">Kobalt build file: </label></th>
        <td><props:textProperty name="<%=PATH_TO_BUILD_FILE%>" className="longField" maxlength="256"/>
            <bs:vcsTree fieldId="<%=PATH_TO_BUILD_FILE%>"/>
            <span class="smallNote">Path to build file</span>
        </td>
    </tr>
    <tr class="advancedSetting">
        <th><label for="ui.kobalt.additional.kobalt.cmd.params">Additional Kobalt command line
            parameters: </label></th>
        <td><props:textProperty name="<%=KOBALT_CMD_PARAMS%>" className="longField" expandable="true"/>
            <span class="smallNote">Additional parameters will be added to the 'Kobalt' command line.<br/>Parameters should be separated with space delimiter</span>
        </td>
    </tr>
    <tr>
        <th><label>Kobalt Wrapper:</label></th>
        <td><props:checkboxProperty name="<%=USE_KOBALT_WRAPPER%>"/>
            <label for="ui.kobalt.runner.wrapper.useWrapper">Use kobalt wrapper to build project</label>
        </td>
    </tr>
    <tr id="ui.kobalt.runner.version.useVersion.tr">
        <th><label>Kobalt Version:</label></th>
        <td><props:selectProperty name="<%=USE_KOBALT_VERSION%>"
                                  enableFilter="true"
                                  className="mediumField">
            <props:option value=""
                          selected="${not kobaltVersionSelectBean.versionSelected}">-- Choose available versions --</props:option>
            <c:forEach items="${kobaltVersionSelectBean.versions}" var="version">
                <props:option value="${version}" selected="${version == kobaltVersionSelectBean.selectedVersion}">
                    <c:out value="${version}"/></props:option>
            </c:forEach>
        </props:selectProperty>
        </td>
    </tr>

</l:settingsGroup>

<l:settingsGroup title="Kobalt Settings" className="advancedSetting">
    <tr class="advancedSetting">
        <th><label for="ui.kobalt.runner.kobalt.settings">Kobalt settings: </label></th>
        <td><props:textProperty name="<%=KOBALT_SETTINGS%>" className="longField" expandable="true"/>
            <span class="smallNote">Enter kobalt settings xml content or leave blank to use the default.</span>
        </td>
    </tr>
</l:settingsGroup>

<props:javaSettings/>

<script type="text/javascript">
    var updateKobaltVersionVisibility = function () {
        var useWrapper = $('ui.kobalt.runner.wrapper.useWrapper').checked;
        if (useWrapper) {
            $('ui.kobalt.runner.version.useVersion').disabled = true;
            BS.Util.hide('ui.kobalt.runner.version.useVersion.tr');
        }
        else {
            $('ui.kobalt.runner.version.useVersion').disabled = false;
            BS.Util.show('ui.kobalt.runner.version.useVersion.tr');
        }
        BS.VisibilityHandlers.updateVisibility($('ui.kobalt.runner.version.useVersion.tr'));
    };

    $j(BS.Util.escapeId("ui.kobalt.runner.wrapper.useWrapper")).click(updateKobaltVersionVisibility);

    updateKobaltVersionVisibility();
</script>