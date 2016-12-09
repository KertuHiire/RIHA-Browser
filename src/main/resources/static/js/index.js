"use strict";

function Browser(infosystemsUrl) {

  var self = this;

  self.init = function() {
    loadInfosystems();
  };

  function loadInfosystems() {
    $.getJSON(infosystemsUrl, function(data) {
      self._createTableRows(data);
      $('#info-systems-table').DataTable({
        language: { "url": "/js/vendor/jquery.dataTables.i18n.json" },
        paging: false,
        initComplete: initColumnFilters
      });
    });
  }

  function initColumnFilters() {
    this.api().columns().every(function () {
      addFilter(this);
    });
  }

  function addInputFilter(column) {
    return $('<input>')
      .appendTo($(column.header()))
      .on('keyup', function () {
        var val = $.fn.dataTable.util.escapeRegex(
          $(this).val()
        );

        column
          .search(val ? val : '', true, false)
          .draw();
      });
  }

  function addSelectFilter(column) {
    var select = $('<select></select>')
      .appendTo($(column.header()))
      .on('change', function () {
        var val = $.fn.dataTable.util.escapeRegex(
          $(this).val()
        );

        column
          .search(val ? '^' + val + '$' : '', true, false)
          .draw();
      });
    column.data().unique().sort().each(function (d, j) {
      select.append('<option value="' + d + '">' + d + '</option>')
    });
    return select;
  }

  function addFilter(column) {
    var filterType = $(column.header()).data('filter');

    if (filterType == 'input') {
      var input = addInputFilter(column);
    }
    else if (filterType == 'select') {
      var select = addSelectFilter(column);
    }
  }

  self._createTableRows = function(data) {
    var template = $('.template-row');

    var tbody = $('tbody');
    data.forEach(function (infosystem) {
      var newRow = $(template).clone().removeClass('hidden').removeClass('template-row');
      newRow.attr('title', JSON.stringify(infosystem));
      newRow.find('.owner').text(infosystem.owner);
      newRow.find('.name').text(infosystem.name);
      newRow.find('.last-modified').text(infosystem.status ? infosystem.status.timestamp : '');
      newRow.find('.status').text(infosystem.status ? infosystem.status.staatus : '');
      newRow.find('.approved').text(infosystem.approval ? infosystem.approval.timestamp : '');
      newRow.find('.approval-status').text(infosystem.approval ? infosystem.approval.status : '');
      tbody.append(newRow);
    });
  }
}