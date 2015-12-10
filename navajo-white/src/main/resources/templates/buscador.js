$(document).ready(
    function() {
        $("#buscador").submit(
            function(event) {

                //var modelAttributeValue = '${listaLocations}';
                    //modelAttributeValue = modelAttributeValue.replace(/"/g, '');
                    //$("#result").html("<p>" + modelAttributeValue + "</p>");

                    var json = null;
                            $.ajax({
                                'async': false,
                                'global': false,
                                'url': "/e4f06474+LOCATIONS",
                                'dataType': "json",
                                'success': function (data) {
                                     json = data;

                                 }
                            });

                    var jsonsplit = json.split(",");
                    $("#result").html("<p>" + jsonsplit[0] + "-" + jsonsplit[1] + "</p>");

                    //var i;
                    //var myLatLng;
                        var myLatlng = new google.maps.LatLng(-25.363882,131.044922);
                        var mapOptions = {
                            zoom: 4,
                            center: myLatlng
                           }
                        var map = new google.maps.Map(document.getElementById("map"), mapOptions);

                        var marker = new google.maps.Marker({
                            position: myLatlng,
                            title:"Hello World!"
                        });

                        marker.setMap(map);

            });
    });