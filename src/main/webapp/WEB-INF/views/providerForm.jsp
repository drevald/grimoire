<%@ include file = "/WEB-INF/adminHeader.jsp"%>

<div class="col-sm-8 p-5">

    <h3 class="mb-4">
        <c:choose>
            <c:when test="${isNew}">Add New Translation Provider</c:when>
            <c:otherwise>Edit Translation Provider</c:otherwise>
        </c:choose>
    </h3>

    <form method="post" action="${pageContext.request.contextPath}/admin/providers/save">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="id" value="${provider.id}"/>

        <div class="mb-3">
            <label for="title" class="form-label">Title *</label>
            <input type="text" class="form-control" id="title" name="title"
                   value="${provider.title}" required maxlength="32"
                   placeholder="e.g., Google Translate">
            <div class="form-text">Display name for this translation provider</div>
        </div>

        <div class="mb-3">
            <label for="method" class="form-label">HTTP Method *</label>
            <select class="form-select" id="method" name="method" required>
                <option value="GET" ${provider.method == 'GET' ? 'selected' : ''}>GET</option>
                <option value="POST" ${provider.method == 'POST' ? 'selected' : ''}>POST</option>
            </select>
            <div class="form-text">HTTP method for API requests</div>
        </div>

        <div class="mb-3">
            <label for="host" class="form-label">Host / API Endpoint</label>
            <input type="text" class="form-control" id="host" name="host"
                   value="${provider.host}" maxlength="64"
                   placeholder="For POST: https://api.example.com/translate">
            <div class="form-text">
                For POST: Full API endpoint URL (e.g., https://api.example.com/translate)<br>
                For GET: Can be empty or base URL
            </div>
        </div>

        <div class="mb-3">
            <label for="reqPattern" class="form-label">Request URL Pattern *</label>
            <textarea class="form-control font-monospace" id="reqPattern" name="reqPattern"
                      rows="2" required maxlength="255"
                      placeholder="http://api.example.com/translate?text={0}&from={1}&to={2}">${provider.reqPattern}</textarea>
            <div class="form-text">
                For GET: Full URL template with placeholders<br>
                For POST: Endpoint URL (can include query params with placeholders)<br>
                Placeholders: {0} = text, {1} = source lang, {2} = target lang
            </div>
        </div>

        <div class="mb-3">
            <label for="requestBody" class="form-label">Request Body Template (for POST)</label>
            <textarea class="form-control font-monospace" id="requestBody" name="requestBody"
                      rows="4" maxlength="1000"
                      placeholder="[{&quot;text&quot;:&quot;{0}&quot;}]">${provider.requestBody}</textarea>
            <div class="form-text">
                Request body template for POST requests (ignored for GET)<br>
                Placeholders: {0} = text, {1} = source lang, {2} = target lang<br>
                Example JSON: [{"text":"{0}"}]
            </div>
        </div>

        <div class="mb-3">
            <label for="resPattern" class="form-label">Response Parser Pattern *</label>
            <input type="text" class="form-control font-monospace" id="resPattern" name="resPattern"
                   value="${provider.resPattern}" required maxlength="255"
                   placeholder="{0}">
            <div class="form-text">
                MessageFormat pattern to extract translation from API response<br>
                Usually {0} to extract the first element
            </div>
        </div>

        <div class="mb-3">
            <label for="contentType" class="form-label">Content-Type (for POST)</label>
            <select class="form-select" id="contentType" name="contentType">
                <option value="application/json" ${provider.contentType == 'application/json' ? 'selected' : ''}>application/json</option>
                <option value="application/x-www-form-urlencoded" ${provider.contentType == 'application/x-www-form-urlencoded' ? 'selected' : ''}>application/x-www-form-urlencoded</option>
                <option value="text/plain" ${provider.contentType == 'text/plain' ? 'selected' : ''}>text/plain</option>
                <option value="application/xml" ${provider.contentType == 'application/xml' ? 'selected' : ''}>application/xml</option>
            </select>
            <div class="form-text">MIME type for POST requests (ignored for GET)</div>
        </div>

        <div class="mb-3">
            <label for="charset" class="form-label">Character Encoding</label>
            <select class="form-select" id="charset" name="charset">
                <option value="UTF-8" ${provider.charset == 'UTF-8' ? 'selected' : ''}>UTF-8</option>
                <option value="ISO-8859-1" ${provider.charset == 'ISO-8859-1' ? 'selected' : ''}>ISO-8859-1</option>
                <option value="Windows-1251" ${provider.charset == 'Windows-1251' ? 'selected' : ''}>Windows-1251</option>
                <option value="Windows-1252" ${provider.charset == 'Windows-1252' ? 'selected' : ''}>Windows-1252</option>
            </select>
            <div class="form-text">Character encoding for POST requests</div>
        </div>

        <div class="mb-3">
            <label for="headers" class="form-label">Custom HTTP Headers (optional)</label>
            <textarea class="form-control font-monospace" id="headers" name="headers"
                      rows="5" maxlength="1000"
                      placeholder="Ocp-Apim-Subscription-Key: your-key-here&#10;Ocp-Apim-Subscription-Region: westeurope&#10;Authorization: Bearer your-token">${provider.headers}</textarea>
            <div class="form-text">
                One header per line, format: <code>Header-Name: value</code><br>
                Lines starting with # are ignored (comments)<br>
                Example for Microsoft Translator: <code>Ocp-Apim-Subscription-Key: YOUR_KEY</code><br>
                Leave empty if no custom headers are needed
            </div>
        </div>

        <div class="mb-3">
            <button type="submit" class="btn btn-primary">
                <c:choose>
                    <c:when test="${isNew}">Create Provider</c:when>
                    <c:otherwise>Update Provider</c:otherwise>
                </c:choose>
            </button>
            <a href="${pageContext.request.contextPath}/admin/providers" class="btn btn-secondary">Cancel</a>
        </div>
    </form>

    <hr class="my-4">

    <div class="alert alert-info">
        <h5>Configuration Examples:</h5>

        <p><strong>GET Request Example:</strong></p>
        <ul class="small font-monospace">
            <li>Method: GET</li>
            <li>Host: (can be empty)</li>
            <li>Request URL Pattern: http://api.example.com/translate?text={0}&from={1}&to={2}</li>
            <li>Request Body: (leave empty for GET)</li>
            <li>Response Parser Pattern: {0}</li>
        </ul>

        <p><strong>POST JSON Example:</strong></p>
        <ul class="small font-monospace">
            <li>Method: POST</li>
            <li>Host: (can be empty or base URL)</li>
            <li>Request URL Pattern: https://api.example.com/translate</li>
            <li>Request Body: [{"text":"{0}"}]</li>
            <li>Response Parser Pattern: {0}</li>
            <li>Content-Type: application/json</li>
            <li>Charset: UTF-8</li>
        </ul>

        <p><strong>Microsoft Translator Example (with custom headers):</strong></p>
        <ul class="small font-monospace">
            <li>Method: POST</li>
            <li>Host: (can be empty)</li>
            <li>Request URL Pattern: https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from={1}&to={2}</li>
            <li>Request Body: [{"text":"{0}"}]</li>
            <li>Response Parser Pattern: {0}</li>
            <li>Content-Type: application/json</li>
            <li>Charset: UTF-8</li>
            <li>Headers:<br>
                Ocp-Apim-Subscription-Key: YOUR_KEY_HERE<br>
                Ocp-Apim-Subscription-Region: westeurope
            </li>
        </ul>
    </div>

</div>

<%@ include file="/WEB-INF/footer.jsp"%>
