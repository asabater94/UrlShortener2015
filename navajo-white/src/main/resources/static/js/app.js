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
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>");
                    },
                    error : function() {
                        $("#result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });

        $("#publi").submit(     //se creará la uri acortada pero iremos a la pagina con publi (++)
            function(event) {
                event.preventDefault();
                $.ajax({
                   type : "POST",
                   url : "/publicidad",
                   data : $(this).serialize(),
                   success : function(msg) {
                       $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "++'>"
                            + msg.uri
                            + "++</a></div>");
                   },
                   error : function() {
                       $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                       }
                 });
            });
    });