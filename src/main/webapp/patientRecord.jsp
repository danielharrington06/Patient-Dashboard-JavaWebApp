<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">
<head>
    <jsp:include page="/meta.jsp"/>
    <title>Patient Data App</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
    <h2>Patient Record</h2>
    <%
        String id = request.getParameter("id");
        String fromEncoded = request.getParameter("from");
        String backHref;
        if (fromEncoded != null && !fromEncoded.isEmpty()) {
            String fromDecoded = URLDecoder.decode(fromEncoded, "UTF-8");
            boolean hasSearch = fromDecoded.contains("searchstring=");
            backHref = (hasSearch ? "/runsearch?" : "/patientList?") + fromDecoded.substring(1);
        } else {
            backHref = "/patientList";
        }
        String fromParam = fromEncoded != null ? fromEncoded : "";
    %>

    <div class="record-actions">
        <a href="<%= backHref %>" class="btn btn-secondary">← Back</a>
        <div class="record-actions-right">
            <a href="/editPatient?id=<%= id %>&from=<%= fromParam %>" class="btn btn-secondary">Edit Patient</a>
            <button type="button" class="btn btn-danger" onclick="document.getElementById('delete-modal').style.display='flex'">
                Delete Patient
            </button>

            <div id="delete-modal" class="modal-overlay">
                <div class="modal">
                    <h3 class="modal-title">Delete Patient</h3>
                    <p class="modal-body">
                        Are you sure you want to delete this patient? This action cannot be undone.
                    </p>
                    <div class="modal-actions">
                        <button type="button" class="btn btn-secondary"
                                onclick="document.getElementById('delete-modal').style.display='none'">
                            Cancel
                        </button>
                        <form method="POST" action="/deletePatient" style="display:inline">
                            <input type="hidden" name="id" value="<%= id %>"/>
                            <input type="hidden" name="from" value="<%= fromParam %>"/>
                            <button type="submit" class="btn btn-danger">Yes, Delete</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
        <p class="error"><%= errorMessage %></p>
    <%
        }
    %>
    <%
        LinkedHashMap<String, String> patientRecord =
            (LinkedHashMap<String, String>) request.getAttribute("patientRecord");
    %>
    <dl>
        <% for (Map.Entry<String, String> entry : patientRecord.entrySet()) { %>
            <dt><%= entry.getKey() %></dt>
            <dd><%= entry.getValue() != null && !entry.getValue().isEmpty() ? entry.getValue() : "—" %></dd>
        <% } %>
    </dl>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>