<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>渠道包管理</title>
</head>

<body>
	<c:if test="${not empty message}">
		<div id="message" class="alert alert-success"><button data-dismiss="alert" class="close">×</button>${message}</div>
	</c:if>
	<div class="row">
		<div class="span4 offset7">
			<%-- <form class="form-search" action="#">
				<label>名称：</label> <input type="text" name="search_LIKE_packetName" class="input-medium" value="${param.search_LIKE_packetName}"> 
				<button type="submit" class="btn" id="search_btn">Search</button>
		    </form> --%>
	    </div>
	    <tags:sort/>
	</div>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>渠道包</th><th>管理</th></tr></thead>
		<tbody>
		<c:forEach items="${packets.content}" var="packet">
			<tr>
				<td><a href="${ctx}/packet/update/${packet.id}">${packet.packetName}</a></td>
				<td><a href="${ctx}/packet/delete/${packet.id}">删除</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<tags:pagination page="${packets}" paginationSize="5"/>

	<div><a class="btn" href="${ctx}/packet/create">添加渠道包</a></div>
</body>
</html>
