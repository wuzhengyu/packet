<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
	<title>渠道包管理</title>
	<script>
		$(document).ready(function() {
			//聚焦第一个输入框
			$("#packetName").focus();
			//为inputForm注册validate函数
			$("#inputForm").validate({
				rules: {
					packetName: {
						remote: "${ctx}/packet/checkPacketName"
					}
				},
				messages: {
					packetName: {
						remote: "渠道包名已存在"
					}
				}
			});
		});
	</script>
	
</head>

<body>
	<form id="inputForm" action="${ctx}/packet/${action}" method="post" class="form-horizontal">
		<input type="hidden" name="id" value="${packet.id}"/>
		<fieldset>
			<legend><small>管理渠道包</small></legend>
			<div class="control-group">
				<label for="packetName" class="control-label">渠道包名称:</label>
				<div class="controls">
					<input type="text" id="packetName" name="packetName"  value="${packet.packetName}" class="input-large required" minlength="3"/>
				</div>
			</div>	
			<div class="control-group">
				<label for="description" class="control-label">渠道包描述:</label>
				<div class="controls">
					<textarea id="description" name="description" class="input-large">${packet.description}</textarea>
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
