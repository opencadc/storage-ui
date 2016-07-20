<!DOCTYPE html>

<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
  <meta name="description" content="User storage system.">
  <meta name="author" content="Canadian Astronomy Data Centre">
  <link rel="icon" href="../../favicon.ico">

  <title>User Storage</title>

  <!-- Bootstrap core CSS -->
  <link href="/storage/css/bootstrap.min.css" rel="stylesheet">
  <link href="/storage/css/bootstrap-theme.min.css" rel="stylesheet">

  <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<#--<link href="../../assets/css/ie10-viewport-bug-workaround.css" rel="stylesheet">-->

  <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

  <!-- Custom CSS -->
  <link rel="stylesheet" href="/storage/css/datatables.min.css"/>
  <link rel="stylesheet" href="/storage/css/storage.css"/>
</head>

<body>

<#assign startTime = .now?time>
<#assign isRoot = folder.root>

<#if username??>
  <#assign homeURL = '/storage/list/${username}'>
</#if>

<#include "_top_nav.ftl">

<div class="container-fluid">

<#include "_main.ftl">

</div><!-- /.container -->


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="/storage/js/jquery.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<#--<script src="/storage/js/ie10-viewport-bug-workaround.js"></script>-->

<script type="text/javascript" src="/storage/js/cadc.util.js"></script>
<script type="text/javascript" src="/storage/js/cadc.uri.js"></script>
<script type="text/javascript" src="/storage/js/datatables.min.js"></script>
<script type="text/javascript" src="/storage/js/file-size.js"></script>
<script type="text/javascript"
        src="/storage/js/jquery.csv-0.71.min.js"></script>
<script type="text/javascript" src="/storage/js/jquery-browser.js"></script>
<script type="text/javascript" src="/storage/js/jquery.form-3.24.js"></script>
<script type="text/javascript"
        src="/storage/js/jquery.contextmenu/jquery.contextMenu-1.01.js"></script>
<script type="text/javascript"
        src="/storage/js/jquery.tablesorter-2.7.2.min.js"></script>
<script type="text/javascript"
        src="/storage/js/jquery-impromptu.min.js"></script>
<script type="text/javascript" src="/storage/js/filemanager.js"></script>

<!--
 AWAYS ensure the bootstrap.min.js comes last!
 The popover will not work otherwise.

 jenkinsd 2016.06.24
-->
<script src="/storage/js/bootstrap.min.js"></script>

<script type="text/javascript">

  var ROW_SELECT_TYPE = "row";
  var lockedIcon =
      "<span class=\"glyphicon glyphicon-lock\"></span> <a href=\"/storage/unlock\" title=\"Unlock to modify.\">Unlock</a>";
  var publicLink =
      "<a href=\"#\" class=\"public_link\" title=\"Change group read access.\">Public</a>";
  var selectButtonGroupID = "delete";
  var deleteButtonHTML = "<span id='" + selectButtonGroupID
                         + "' class='btn-group btn-group-xs'>"
                         + "<button id='delete' name='delete' class='btn btn-danger'><span class='glyphicon glyphicon-remove-circle'></span>&nbsp;Delete</button>"
                         + "</span>";
  var stringUtil = new cadc.web.util.StringUtil(publicLink);
  var startURI = "<#if startURI??>${startURI}</#if>";
  var url = "/storage/page${folder.path}";
  var defaultPageSize = 400;

  var requestData = {};

  requestData.pageSize = defaultPageSize;

  if (startURI != "")
  {
    requestData.startURI = encodeURIComponent(startURI);
  }

  $(document).ready(function ()
                    {
                    <#-- Intercept the JavaScript here for the Folder Details button. -->
                    <#if !isRoot>
                      // Activate the Details button.
                      $('[data-toggle="popover"]').popover(
                          {
                            html: true,
                            title: "<strong>${folder.name}</strong>",
                            content: function ()
                            {
                              return '<table class="table table-condensed table-bordered">'
                                     +
                                     '<tbody><tr><td>Owned by</td><td class="info"><strong>${folder.owner}</strong></td></tr>'
                                     +
                                     '<tr><td>Last used</td><td class="info">${folder.lastModifiedHumanReadable}</td></tr>'
                                     + '</tbody></table>';
                            }
                          });
                    </#if>

                      // Override CSS for search filter field.
                      $.fn.DataTable.ext.oStdClasses.sFilter =
                          "dataTables_filter";
                      var $beaconTable = $("#beacon");

                      // For quick pre-load.
                      var rows = [];
                    <#list initialRows as row>
                      rows.push([${row}]);
                    </#list>

                      var $dt = $beaconTable.DataTable(
                          {
                            data: rows,
                            language: {
                              search: "_INPUT_",
                              searchPlaceholder: "Search Name..."
                            },
                            dom: "<'row beacon-info-row'<'col-sm-12'i>>"
                                 + "<'row'<'col-sm-12'tr>>",
                            loading: true,
                            processing: true,
                            deferRender: true,
                            scrollY: "75vh",
                            lengthChange: false,
                            scrollCollapse: true,
                            scroller: true,
                            columnDefs: [
                              {
                                "targets": 0,
                                "orderable": false,
                                "className": 'select-checkbox',
                                "searchable": false,
                                "render": function (data, type, full)
                                {
                                  var renderedValue;

                                  if (full.length > 6)
                                  {
                                    var lockedFlag = (full[7] === "true");

                                    renderedValue = lockedFlag
                                        ? lockedIcon : data;
                                  }
                                  else
                                  {
                                    renderedValue = data;
                                  }

                                  return renderedValue;
                                }
                              },
                              {
                                "targets": 1,
                                "render": function (data, type, full)
                                {
                                  if (full.length > 10)
                                  {
                                    return '<span class="glyphicon ' + full[8]
                                           + '"></span> <a href="/storage'
                                           + full[11]
                                           + '">' + data + '</a>';
                                  }
                                  else
                                  {
                                    return data;
                                  }
                                }
                              },
                              {
                                "targets": 2,
                                "type": "file-size",
                                "searchable": false
                              },
                              {
                                "targets": 5,
                                "searchable": false,
                                "render": function (data, type, full)
                                {
                                  var renderedValue;

                                  if (full.length > 9)
                                  {
                                    var publicFlag = (full[6] === "true");
                                    var path = full[9];

                                    if (publicFlag === true)
                                    {
                                      renderedValue = stringUtil.format(path);
                                    }
                                    else
                                    {
                                      renderedValue = data;
                                    }
                                  }
                                  else
                                  {
                                    renderedValue = data;
                                  }

                                  return renderedValue;
                                }
                              },
                              {
                                "targets": [3, 4],
                                "searchable": false
                              }
                            ],
                            select: {
                              style: 'os',
                              selector: 'td:first-child'
                            },
                            order: [[3, 'desc']]
                          });

                      var deleteLinkContainerSelector =
                          "[id='" + selectButtonGroupID + "']";

                      $dt.on("select", function (event, dataTablesAPI, type,
                                                 indexes)
                      {
                        var $info = $(".dataTables_info");

                        if (type === ROW_SELECT_TYPE)
                        {
                          $info.find(deleteLinkContainerSelector).remove();
                          $info.append(deleteButtonHTML);
                        }
                      });

                      $dt.on("draw.dtSelect.dt select.dtSelect.dt deselect.dtSelect.dt info.dt", function ()
                      {
                        if ($dt.rows({selected: true}).count() > 0)
                        {
                          var $info = $(".dataTables_info");

                          $info.find(deleteLinkContainerSelector).remove();
                          $info.append(deleteButtonHTML);
                        }
                      });

                      $dt.on("deselect", function (event, dataTablesAPI, type,
                                                   indexes)
                      {
                        // If the indexes.length is 1, this that last item is
                        // being removed (deselected).
                        if ((type === ROW_SELECT_TYPE)
                            && (indexes.length === 1))
                        {
                          $(".dataTables_info")
                              .find(deleteLinkContainerSelector).remove();
                        }
                      });

                      $(document).on("click", "a#logout", function()
                      {
                        $.ajax({
                                url: '/storage/ac/authenticate',
                                method: 'DELETE'
                               })
                            .done(function()
                                  {
                                    window.location.reload(true);
                                  });
                      });

                      $(document).on("click", "button#delete",
                                     function ()
                                     {
                                       $.each($dt.rows({selected: true}).data(),
                                              function (key, data)
                                              {
                                                var path = (data.length > 8)
                                                    ? data[9]
                                                    : $(data[0]).data("path");

                                                $.ajax({
                                                         method: "DELETE",
                                                         url: "/storage/item"
                                                              + path
                                                       });
                                              });
                                     });

                      /**
                       * We're putting a custom search field in, so we need to
                       * initialize the searching here.
                       */
                      $("input.dataTables_filter").on("keyup",
                                                      function ()
                                                      {
                                                        $dt.search($(this).val()).draw();
                                                      });

                      var successCallback = function (csvData)
                      {
                        var data = $.csv.toArrays(csvData);
                        var dl = data.length;

                        if (dl > 0)
                        {
                          for (var di = 0; di < dl; di++)
                          {
                            var nextRow = data[di];
                            $dt.row.add(nextRow);

                            startURI = nextRow[10];
                          }

                          $dt.draw();

                          requestData.startURI = startURI;
                          getPage(requestData, successCallback);
                        }
                      };

                      load(successCallback);
                    });

  function load(_callback)
  {
    getPage(requestData, _callback);
  }

  function getPage(_data, _callback)
  {
    $.get({
            url: url,
            dataType: "text",
            data: _data
          })
        .done(function (csvData)
              {
                _callback(csvData);
              });
  }
</script>
</body>
</html>

