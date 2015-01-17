<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<html>
<head>
	<title>渠道管理</title>
</head>

<body>
	<c:if test="${not empty message}">
		<div id="message" class="alert alert-success"><button data-dismiss="alert" class="close">×</button>${message}</div>
	</c:if>
	<div class="row">
		<div class="span4 offset7">
			<%-- <form class="form-search" action="#">
				<label>名称：</label> <input type="text" name="search_LIKE_channelName" class="input-medium" value="${param.search_LIKE_channelName}"> 
				<button type="submit" class="btn" id="search_btn">Search</button>
		    </form> --%>
	    </div>
	    <tags:sort/>
	</div>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>渠道</th><th>管理</th></tr></thead>
		<tbody>
		<c:forEach items="${channels.content}" var="channel">
			<tr>
				<td><a href="${ctx}/channel/update/${channel.id}">${channel.channelName}</a></td>
				<td><a href="${ctx}/channel/delete/${channel.id}">删除</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<tags:pagination page="${channels}" paginationSize="5"/>

	<div><a class="btn" href="${ctx}/channel/create">添加渠道</a></div>
</body>
</html>
