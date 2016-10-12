<%@ page import="static com.buildServer.kobalt.common.KobaltRunnerConstants.PATH_TO_BUILD_FILE" %>
<%@ page import="static com.buildServer.kobalt.common.KobaltRunnerConstants.KOBALT_TASKS" %>
<%@ page import="static com.buildServer.kobalt.common.KobaltRunnerConstants.USE_KOBALT_WRAPPER" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>

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
    <tr>
        <th><label>Kobalt Wrapper:</label></th>
        <td><props:checkboxProperty name="<%=USE_KOBALT_WRAPPER%>"/>
            <label for="ui.kobalt.runner.wrapper.useWrapper">Use kobalt wrapper to build project</label>
        </td>
    </tr>

</l:settingsGroup>

<props:javaSettings/>
