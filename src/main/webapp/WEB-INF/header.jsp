<%@ page language="java" pageEncoding="utf-8" contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<link rel="icon" type="image/png" href="/grimoire/favicon.png" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf8">
	<title><spring:message code="title" /></title>
    <script>
    /*
        var i = 0;
        var words = [];
        function show(x) {
            element = document.getElementById(i);
            element.style.backgroundColor="white";
            if (event.keyCode == 37) {
                i--;
            }
            if (event.keyCode == 39) {
                i++;
            }
            element = document.getElementById(i);
            element.style.backgroundColor="yellow";
            element1 = document.getElementById("result");
            element1.innerText = words[i];
        }
    */
    </script>
</head>
<body>
    <form:errors />
