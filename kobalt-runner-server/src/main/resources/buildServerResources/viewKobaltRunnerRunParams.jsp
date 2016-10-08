<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<div class="parameter">
    Kobalt tasks: <strong><props:displayValue name="ui.kobalt.runner.kobalt.tasks" emptyValue="default"/></strong>
</div>

<props:viewJavaHome/>
<props:viewJvmArgs/>