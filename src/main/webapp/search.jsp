<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Data App - Search</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
    <h2>Search Patients</h2>
    <div class="search-card">
        <form method="GET" action="/runsearch">
            <div class="search-row">
                <input class="search-input" type="text" name="searchstring" placeholder="Enter search keyword..."/>
                <input class="btn" type="submit" value="Search"/>
            </div>
        </form>
    </div>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>