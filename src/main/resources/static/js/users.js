var usersUrl = "/api/v1/users";

$(function () {
    var userRow;

    $('body').on('click', '.delete-user', function () {
        userRow = $(this).closest('tr');
        var username = userRow.children('.username').text();
        $('#delete-user-message').text(`Are you sure you want delete user ${username}?`);
        $('#delete-user-confirm').modal();
    });

    $("#confirm-delete-user").on("click", function () {
        $("#delete-user-confirm").modal('hide');
        var username = userRow.children('.username').text();
        deleteUser(username);
    });
});

function updateUserList() {
    $.getJSON(usersUrl, function (data) {
        $('#user-list').html('');
        if (data.empty) {
            $('.user-table').hide();
            $('#empty-list').show();
        } else {
            $.each(data.content, function (index, user) {
                $('#user-list').append(getUserData(user));
            });
            $('.user-table').show();
            $('#empty-list').hide();
        }
    });
}

function getRoleNames(roles) {
    var roleNames = [];
    $.each(roles, function (index, role) {
        roleNames.push(role.name);
    });
    return roleNames.join(', ');
}

function getUpdateButton(user) {
    return $('<a class="btn btn-info btn-sm text-white update-user">Update</a>')
        .attr('href', "/users/edit?userId=" + user.id);
}

function getDeleteButton(user) {
    return $('<a class="btn btn-danger btn-sm ml-1 text-white delete-user">Delete</a>');
}

function getActionButtons(user) {
    var warnMessage = "Вы уверены что хотите удалить пользователя " + user.username + "?";
    return $('<td class="text-nowrap"></td>')
        .append(getUpdateButton(user))
        .append(getDeleteButton(user));
}

function getUserData(user) {
    var userId = user.id;
    var username = user.username;
    var password = user.password;
    var active = user.active;
    var roles = getRoleNames(user.roles);
    var actionButtons = getActionButtons(user);

    return $('<tr></tr>')
        .append($('<td class="username"></td>').text(username))
        .append($('<td></td>').text(password))
        .append($('<td></td>').text(active))
        .append($('<td class="text-nowrap"></td>').text(roles))
        .append(actionButtons);
}

function deleteUser(username) {
    $.ajax(usersUrl + `/${username}`, {
        method: 'DELETE',
        success: function () {
            updateUserList();
        }
    });
}