$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                var num = document.getElementById("numeroClicks").value;
                $.ajax({
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + num
                            + "'>"
                            + num
                            + "</a></div>");
                });
            });
    });