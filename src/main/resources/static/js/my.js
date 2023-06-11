
const toggleSidebar = () => {
    if ($(".sidebar").is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    }
    else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};

const search = () => {
    let query = $("#search-input").val();
    if (query == "") {
        $(".search-result").hide();
    }
    else {
        // console.log(query);
        let url = `http://localhost:8080/search/${query}`;
        fetch(url)
            .then(response => {
                return response.json();
            })
            .then((data) => {
                //check data are coming or not from database
                // console.log(data);


                //convert data into html
                //open div
                let text = `<div class='list-group'>`

                data.forEach((contact) => {
                    text += `<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`
                });

                // close div
                text += `</div>`;
                $(".search-result").html(text);
                $(".search-result").show();
            });
    }

};