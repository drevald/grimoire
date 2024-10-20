<%@ page language="java" pageEncoding="utf-8" contentType="text/html; charset=utf-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap 5 Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

    <!--
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M" crossorigin="anonymous">
    <link href="https://getbootstrap.com/docs/4.0/examples/signin/signin.css" rel="stylesheet" crossorigin="anonymous"/>
    -->

    <link href="/favicon.ico" rel="icon" type="image/x-icon" />
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
<div class="container-fluid fixed-top">
  <div class="row">
        <form:errors />
