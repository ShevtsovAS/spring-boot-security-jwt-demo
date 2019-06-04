var usersUrl = "/api/v1/users";

$(function () {
    $('body').on('click', '.delete-user', function () {
        var username = $(this).closest('tr').children('.username').text();
        $('#delete-user-message').text(`Are you sure you want delete ${username}?`);
        $("#confirm-delete-user").attr('onclick', `deleteUser('${username}')`);
        $('#delete-user-confirm').modal();
    });

    $('body').on('click', '.update-user', function () {
        var userId = $(this).closest('tr').children('.userId').val();
        var username = $(this).closest('tr').children('.username').text();
        var active = $(this).closest('tr').children('.active').text() == 'true';
        var roles = $(this).closest('tr').children('.roles').text();
        $('#add-user-form').children(':input[name="userId"]').val(userId);
        $('#add-user-form').children(':input[name="username"]').val(username);
        $('#add-user-form').children(':input[name="password"]').prop('type', 'hidden');
        $('#add-user-form').children(':input[name="roles"]').val(roles);
        $('#active').prop('checked', active);
        $('#add-user-modal').modal();
    });

    $('#save-user-button').click(function () {
        var userData = getUserJson($('#add-user-form :input'));
        !(userData.userId.trim()) ? createUser(userData) : updateUser(userData);
    });

    $('#add-user-modal').on('hidden.bs.modal', function (e) {
        $('#add-user-form :input').val('');
        $('#active').val("true").prop('checked', true);
        $('#add-user-form').children(':input[name="password"]').prop('type', 'password');
    });
});

function updateUser(userData) {
    $.ajax(`${usersUrl}/${userData.userId}`, {
        data: JSON.stringify(userData),
        contentType: 'application/json',
        type: 'PUT',
        success: function (data) {
            updateUserList();
        }
    });
}

function createUser(userData) {
    $.ajax(usersUrl, {
        data: JSON.stringify(userData),
        contentType: 'application/json',
        type: 'POST',
        success: function (data) {
            updateUserList();
        }
    });
}

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
            $('#empty-list').hide();
            $('.user-table').show();
        }
    });
}

function getRoleNames(roles) {
    return $.map(roles, function (role) {
        return role.name;
    }).join(', ');
}

function getUpdateButton(user) {
    return $('<a class="btn btn-info btn-sm text-white update-user">Update</a>');
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
    return $('<tr></tr>')
        .append($('<input class="userId" type="hidden">').val(user.id))
        .append($(`<td class="username">${user.username}</td>`))
        .append($(`<td class="password">${user.password}</td>`))
        .append($(`<td class="active">${user.active}</td>`))
        .append($(`<td class="roles text-nowrap">${getRoleNames(user.roles)}</td>`))
        .append(getActionButtons(user));
}

function deleteUser(username) {
    $.ajax(`${usersUrl}/${username}`, {
        method: 'DELETE',
        success: function () {
            updateUserList();
        }
    });
}

function getUserJson(userFields) {
    var data = {};
    $(userFields.serializeArray()).each(function () {
        if (this.name == 'roles') {
            var roles = [];
            $(this.value.split(",")).each(function () {
                roles.push($.trim(this));
            })
            data[this.name] = roles;
        } else {
            data[this.name] = this.value;
        }
    });
    return data;
}