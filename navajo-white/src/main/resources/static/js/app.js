$(document).ready(
    function() {
        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/link",
                    data : $(this).serialize(),
                    success : function(msg) {
                        if (msg.active == 0) {
                            $("#result").html(
                                "<div class='alert alert-success lead'>Try with a correct URL or try again later</div>");
                        }
                        else {
                            $("#result").html(
                                "<div class='alert alert-success lead'><a target='_blank' href='"
                                + msg.uri
                                + "'>"
                                + msg.uri
                                + "</a></div>");
                        }
                    },
                    error : function() {
                        $("#result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
    });