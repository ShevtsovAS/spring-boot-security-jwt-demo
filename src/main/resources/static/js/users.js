var usersUrl = "/api/v1/users";
var dictionaryUrl = "/api/v1/dictionaries";
var currentPage = 0;
var pageSize = 10;
var defaultSort = "username,asc";

$(function () {
    $('body').on('click', '.page-item', function () {
        return false;
    });

    initRolesMultiselect();

    $('body').on('click', '.delete-user', function () {
        var username = $(this).closest('tr').children('.username').text();
        $('#delete-user-message').text(`Are you sure you want delete ${username}?`);
        $("#confirm-delete-user").attr('onclick', `deleteUser('${username}')`);
        $('#delete-user-confirm').modal();
    });

    $('body').on('click', '.update-user', function () {
        var userId = $(this).closest('tr').children('.userId').val();
        var username = $(this).closest('tr').children('.username').text();
        var active = $(this).closest('tr').children('.active').text() == 'YES';
        var roles = $(this).closest('tr').children('.roles').text().split(', ');
        $('#add-user-form').children(':input[name="userId"]').val(userId);
        $('#add-user-form').children(':input[name="username"]').val(username);
        $('#add-user-form').children(':input[name="password"]').prop('type', 'hidden');
        $('#multiple-checkboxes').val(roles).selectpicker("refresh");
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
        $('#multiple-checkboxes').selectpicker("refresh");
    });
});

function updateUser(userData) {
    $.ajax(`${usersUrl}/${userData.userId}`, {
        data: JSON.stringify(userData),
        contentType: 'application/json',
        type: 'PUT',
        success: function (data) {
            loadUserList();
        }
    });
}

function createUser(userData) {
    $.ajax(usersUrl, {
        data: JSON.stringify(userData),
        contentType: 'application/json',
        type: 'POST',
        success: function (data) {
            loadUserList();
        }
    });
}

function loadUserList() {
    updateUserList(currentPage, pageSize, defaultSort);
}

function updateUserList(pageNum, size, sort) {
    $.getJSON(usersUrl, {
            page: pageNum,
            size: size,
            sort: sort
        },
        function (page) {
            $('#user-list').html('');
            if (page.empty) {
                $('.user-table').hide();
                $('#empty-list').show();
            } else {
                $.each(page.content, function () {
                    $('#user-list').append(getUserData(this));
                });
                $('#empty-list').hide();
                $('.user-table').show();
                if (page.totalPages > 1) {
                    showUserPagination(page, size, sort);
                } else {
                    $('#user-pagination').hide();
                }
            }
        }
    );
    currentPage = pageNum;
}

function showUserPagination(page, size, sort) {
    var paging = {
        firstPage: $('#user-pagination').find('.first'),
        prevPage: $('#user-pagination').find('.previous'),
        leftItem: $('#user-pagination').find('.left-item'),
        centerItem: $('#user-pagination').find('.center-item'),
        rightItem: $('#user-pagination').find('.right-item'),
        nextPage: $('#user-pagination').find('.next'),
        lastPage: $('#user-pagination').find('.last'),
        pageIndex: page.number,
        pageNum: page.number + 1,
        lastPageIndex: page.totalPages - 1,
        totalPages: page.totalPages,
        size: size,
        sort: sort
    }

    paging.leftItem.show();
    paging.rightItem.show();

    if (page.first) {
        firstPageInit(paging);
    } else if (page.last) {
        lastPageInit(paging);
    } else {
        currentPageInit(paging);
    }

    paging.lastPage.children('.page-link').text(`Last (${page.totalPages})`);

    $('#user-pagination').show();
}

function firstPageInit(paging) {
    paging.firstPage.addClass('disabled').children('.page-link')
        .removeAttr('onclick');
    paging.prevPage.addClass('disabled').children('.page-link')
        .removeAttr('onclick');
    paging.leftItem.addClass('active').children('.page-link')
        .removeAttr('onclick')
        .text(`${paging.pageNum}`);
    paging.centerItem.removeClass('active').children('.page-link')
        .attr('onclick', `updateUserList(${paging.pageNum}, ${paging.size}, '${paging.sort}')`)
        .text(`${paging.pageNum + 1}`);
    paging.rightItem.removeClass('active').children('.page-link')
        .attr('onclick', `updateUserList(${paging.pageNum + 1}, ${paging.size}, '${paging.sort}')`)
        .text(`${paging.pageNum + 2}`);
    paging.nextPage.removeClass('disabled').children('.page-link')
        .attr('onclick', `updateUserList(${paging.pageNum}, ${paging.size}, '${paging.sort}')`);
    paging.lastPage.removeClass('disabled').children('.page-link')
        .attr('onclick', `updateUserList(${paging.lastPageIndex}, ${paging.size}, '${paging.sort}')`);

    if (paging.totalPages < 3) {
        paging.rightItem.hide();
    }
}

function lastPageInit(paging) {
    paging.firstPage.removeClass('disabled').children('.page-link')
        .attr('onclick', `updateUserList(0, ${paging.size}, '${paging.sort}')`);
    paging.prevPage.removeClass('disabled').children('.page-link')
        .attr('onclick', `updateUserList(${paging.pageIndex - 1}, ${paging.size}, '${paging.sort}')`);
    paging.leftItem.removeClass('active').children('.page-link')
        .attr('onclick', `updateUserList(${paging.pageIndex - 2}, ${paging.size}, '${paging.sort}')`)
        .text(`${paging.pageNum - 2}`);
    paging.centerItem.removeClass('active').children('.page-link')
        .attr('onclick', `updateUserList(${paging.pageIndex - 1}, ${paging.size}, '${paging.sort}')`)
        .text(`${paging.pageNum - 1}`);
    paging.rightItem.addClass('active').children('.page-link')
        .removeAttr('onclick')
        .text(`${paging.pageNum}`);
    paging.nextPage.addClass('disabled').children('.page-link')
        .removeAttr('onclick');
    paging.lastPage.addClass('disabled').children('.page-link')
        .removeAttr('onclick');

    if (paging.totalPages < 3) {
        paging.leftItem.hide();
    }
}

function currentPageInit(paging) {
    paging.firstPage.removeClass('disabled').children('.page-link')
        .attr('onclick', `updateUserList(0, ${paging.size}, '${paging.sort}')`);
    paging.prevPage.removeClass('disabled').children('.page-link')
        .attr('onclick', `updateUserList(${paging.pageIndex - 1}, ${paging.size}, '${paging.sort}')`);
    paging.leftItem.removeClass('active').children('.page-link')
        .attr('onclick', `updateUserList(${paging.pageIndex - 1}, ${paging.size}, '${paging.sort}')`)
        .text(`${paging.pageNum - 1}`);
    paging.centerItem.addClass('active').children('.page-link')
        .removeAttr('onclick')
        .text(`${paging.pageNum}`);
    paging.rightItem.removeClass('active').children('.page-link')
        .attr('onclick', `updateUserList(${paging.pageIndex + 1}, ${paging.size}, '${paging.sort}')`)
        .text(`${paging.pageNum + 1}`);
    paging.nextPage.removeClass('disabled').children('.page-link')
        .attr('onclick', `updateUserList(${paging.pageIndex + 1}, ${paging.size}, '${paging.sort}')`);
    paging.lastPage.removeClass('disabled').children('.page-link')
        .attr('onclick', `updateUserList(${paging.lastPageIndex}, ${paging.size}, '${paging.sort}')`);
}

function getRoleNames(roles) {
    return $.map(roles, function (role) {
        return role.name;
    }).join(', ');
}

function getUpdateButton(user) {
    return $('<a class="btn btn-info btn-sm text-white update-user">').text('Update');
}

function getDeleteButton(user) {
    return $('<a class="btn btn-danger btn-sm ml-1 text-white delete-user">').text('Delete');
}

function getActionButtons(user) {
    return $('<td class="text-nowrap">')
        .append(getUpdateButton(user))
        .append(getDeleteButton(user));
}

function getUserData(user) {
    var active = user.active ? 'YES' : 'NO';
    var activeColor = user.active ? 'green' : 'red';
    return $('<tr></tr>')
        .append($('<input class="userId" type="hidden">').val(user.id))
        .append($(`<td class="username">${user.username}</td>`))
        .append($(`<td class="password">${user.password}</td>`))
        .append($(`<td class="active font-weight-bold">${active}</td>`).addClass(activeColor))
        .append($(`<td class="roles text-nowrap">${getRoleNames(user.roles)}</td>`))
        .append(getActionButtons(user));
}

function deleteUser(username) {
    $.ajax(`${usersUrl}/${username}`, {
        method: 'DELETE',
        success: function () {
            loadUserList();
        }
    });
}

function getUserJson(userFields) {
    var data = {};
    var roles = [];
    $(userFields.serializeArray()).each(function () {
        if (this.name == 'roles') {
            roles.push(this.value);
            data[this.name] = roles;
        } else {
            data[this.name] = this.value;
        }
    });
    return data;
}

function initRolesMultiselect() {
    $.getJSON(`${dictionaryUrl}/roles`, function (data) {
        $.each(data.properties, function () {
            $('#multiple-checkboxes').append(`<option value="${this.name}">${this.value}</option>`);
        });
        $('#multiple-checkboxes')
            .selectpicker({noneSelectedText: 'Roles'})
            .selectpicker("refresh");
    });
}