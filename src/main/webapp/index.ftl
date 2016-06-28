<!DOCTYPE html>

<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
  <meta name="description" content="">
  <meta name="author" content="">
  <link rel="icon" href="../../favicon.ico">

  <title>VOSpace browser</title>

  <!-- Bootstrap core CSS -->
  <link href="/beacon/css/bootstrap.min.css" rel="stylesheet">
  <link href="/beacon/css/bootstrap-theme.min.css" rel="stylesheet">

  <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<#--<link href="../../assets/css/ie10-viewport-bug-workaround.css" rel="stylesheet">-->

  <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->

  <!-- Custom CSS -->
  <link rel="stylesheet" href="/beacon/css/datatables.css"/>
  <link rel="stylesheet" href="/beacon/css/beacon.css"/>
</head>

<body>

<#assign startTime = .now?time>
<#assign isRoot = folder.root>

<#if username??>
  <#assign homeURL = '/beacon/list/${username}'>
</#if>

<#include "_top_nav.ftl">

<div class="container-fluid">

<#include "_main.ftl">

</div><!-- /.container -->


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="/beacon/js/jquery.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<#--<script src="/beacon/js/ie10-viewport-bug-workaround.js"></script>-->

<script type="text/javascript" src="/beacon/js/cadc.util.js"></script>
<script type="text/javascript" src="/beacon/js/cadc.uri.js"></script>
<script type="text/javascript" src="/beacon/js/datatables.js"></script>
<script type="text/javascript" src="/beacon/js/file-size.js"></script>
<script type="text/javascript" src="/beacon/js/jquery.csv-0.71.min.js"></script>
<script type="text/javascript" src="/beacon/js/jquery-browser.js"></script>
<script type="text/javascript" src="/beacon/js/jquery.form-3.24.js"></script>
<script type="text/javascript"
        src="/beacon/js/jquery.contextmenu/jquery.contextMenu-1.01.js"></script>
<script type="text/javascript"
        src="/beacon/js/jquery.tablesorter-2.7.2.min.js"></script>
<script type="text/javascript"
        src="/beacon/js/jquery.impromptu-3.2.min.js"></script>
<script type="text/javascript" src="/beacon/js/filemanager.js"></script>

<!--
 AWAYS ensure the bootstram.min.js comes last!
 The popover will not work otherwise.

 jenkinsd 2016.06.24
-->
<script src="/beacon/js/bootstrap.min.js"></script>

<script type="text/javascript">

  var lockedIcon =
      "<span class=\"glyphicon glyphicon-lock\"></span> <a href=\"/beacon/unlock\" title=\"Unlock to modify.\">Unlock</a>";
  var publicLink =
      "<a href=\"#\" class=\"public_link\" title=\"Change group read access.\">Public</a>";
  var stringUtil = new cadc.web.util.StringUtil(publicLink);
  var startURI = "<#if startURI??>${startURI}</#if>";
  var url = "/beacon/page${folder.path}";
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
                            content: function()
                            {
                              return '<table class="table table-condensed table-bordered">'
                                     + '<tbody><tr><td>Owned by</td><td class="info"><strong>${folder.owner}</strong></td></tr>'
                                     + '<tr><td>Last used</td><td class="info">${folder.lastModifiedHumanReadable}</td></tr>'
                                     + '</tbody></table>';
                            }
                          });
</#if>

                      // Override CSS for search filter field.
                      $.fn.DataTable.ext.oStdClasses.sFilter =
                          "dataTables_filter";
                      var $beaconTable = $("#beacon");

                      var $dt = $beaconTable.DataTable(
                          {
                            language: {
                              search: "_INPUT_",
                              searchPlaceholder: "Search Name..."
                            },
                            dom: "<'row'<'col-sm-12'i>>"
                                 + "<'row'<'col-sm-12'tr>>",
                            loading: true,
                            processing: true,
                            deferRender: true,
                            scrollY: "620px",
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
                                           + '"></span> <a href="/beacon'
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

                        if (data.length > 0)
                        {
                          for (var di = 0, dl = data.length; di < dl; di++)
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

