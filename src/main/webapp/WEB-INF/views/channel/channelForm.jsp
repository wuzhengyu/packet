<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>渠道管理</title>
	<script>
		$(document).ready(function() {
			//聚焦第一个输入框
			$("#channelName").focus();
			//为inputForm注册validate函数
			$("#inputForm").validate({
				rules: {
					channelName: {
						remote: "${ctx}/channel/checkChannelName"
					}
				},
				messages: {
					channelName: {
						remote: "渠道包名已存在"
					}
				}
			});
		});
	</script>
</head>

<body>
	<form id="inputForm" action="${ctx}/channel/${action}" method="post" class="form-horizontal">
		<input type="hidden" name="id" value="${channel.id}"/>
		<fieldset>
			<legend><small>管理渠道</small></legend>
			<div class="control-group">
				<label for="channelName" class="control-label">渠道名称:</label>
				<div class="controls">
					<input type="text" id="channelName" name="channelName"  value="${channel.channelName}" class="input-large required" minlength="3"/>
				</div>
			</div>	
			<div class="control-group">
				<label for="description" class="control-label">渠道描述:</label>
				<div class="controls">
					<textarea id="description" name="description" class="input-large">${channel.description}</textarea>
				</div>
			</div>	
			<div class="form-actions">
				<input id="submit_btn" class="btn btn-primary" type="submit" value="提交"/>&nbsp;	
				<input id="cancel_btn" class="btn" type="button" value="返回" onclick="history.back()"/>
			</div>
		</fieldset>
	</form>
	
</body>
</html>
