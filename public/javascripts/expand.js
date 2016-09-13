jQuery(function($){
                $('#searchicon').click(function(){
                    $('#searchbar').toggleClass('sear-open');
                });
 
                $(document).click(function(e){
                    if(!$(e.target).closest('.navsearch').length){
                        $('#searchbar').removeClass('sear-open');
                    }
                })
            });