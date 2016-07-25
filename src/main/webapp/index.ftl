<!DOCTYPE html>

<html lang="en">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
  <meta name="description" content="User storage system.">
  <meta name="author" content="Canadian Astronomy Data Centre">
  <meta name="keywords" content="VOSpace, IVOA, CADC, Canadian Astronomy Data Centre">
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

                      // For quick pre-load.
                      var rows = [];
                    <#list initialRows as row>
                      rows.push([${row}]);
                    </#list>

                      fileManager(rows, $("#beacon"),
                              "<#if startURI??>${startURI}</#if>",
                              "${folder.path}");

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
                    });
</script>
</body>
</html>

