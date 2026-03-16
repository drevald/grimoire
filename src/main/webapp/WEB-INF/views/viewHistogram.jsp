<%@ page import="org.helico.domain.TranslatorProvider" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/dictHeader.jsp" %>

<div class="col-sm-8 p-5">

    <h3 class="mb-5">Words occurances per hundred</h3>

    <c:if test="${empty histogram}">
        <div class="mb-5">
            No words
        </div>
    </c:if>

    <c:if test="${!empty histogram}">
        <div class="mb-5">
            <canvas id="histogramChart" style="max-width: 600px; max-height: 600px;"></canvas>
        </div>

        <div class="mb-5">
            <table class="table table-condensed table-sm">
                <tr>
                    <th>Nth hundred</th>
                    <th>Words occurances</th>
                    <th>%</th>
                </tr>
                <c:forEach items="${histogram}" var="item">
                <tr>
                    <td>${item.key}</td>
                    <td>${item.value}</td>
                    <td><script>document.write(Math.floor((100.0 * ${item.value}) / ${total}));</script></td>
                </tr>
                </c:forEach>
            </table>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
        <script>
            const ctx = document.getElementById('histogramChart');

            // Prepare data from server-side
            const labels = [];
            const data = [];
            const percentages = [];

            <c:forEach items="${histogram}" var="item">
                labels.push('${item.key}00-${item.key}99');
                data.push(${item.value});
                percentages.push(Math.floor((100.0 * ${item.value}) / ${total}));
            </c:forEach>

            // Generate colors
            const colors = [
                'rgba(255, 99, 132, 0.8)',
                'rgba(54, 162, 235, 0.8)',
                'rgba(255, 206, 86, 0.8)',
                'rgba(75, 192, 192, 0.8)',
                'rgba(153, 102, 255, 0.8)',
                'rgba(255, 159, 64, 0.8)',
                'rgba(199, 199, 199, 0.8)',
                'rgba(83, 102, 255, 0.8)',
                'rgba(255, 99, 255, 0.8)',
                'rgba(99, 255, 132, 0.8)'
            ];

            new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Word Occurrences',
                        data: data,
                        backgroundColor: colors.slice(0, labels.length),
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {
                            position: 'right',
                        },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    const label = context.label || '';
                                    const value = context.parsed || 0;
                                    const percentage = percentages[context.dataIndex];
                                    return label + ': ' + value + ' (' + percentage + '%)';
                                }
                            }
                        }
                    }
                }
            });
        </script>
    </c:if>

</div>

<%@ include file="/WEB-INF/footer.jsp" %>