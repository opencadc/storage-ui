<#assign baseURL = 'http://www.canfar.net'>

<nav id="top_nav" class="navbar navbar-default navbar-fixed-top">
  <div class="container">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#canfar_navbar_collapse" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
      </button>
      <a class="navbar-brand" href="${baseURL}/en"><img src="${baseURL}/css/images/logo.png" /></a>
    </div>

    <div id="canfar_navbar_collapse" class="collapse navbar-collapse">
      <ul id="navbar_list" class="nav navbar-nav pull-right">
        <li class="dropdown">
          <a href="${baseURL}/en/nodes" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Nodes <span class="caret"></span></a>
          <ul class="dropdown-menu list-unstyled">
            <li><a href="${baseURL}/en/nodes/nugrid" class="nodes.nugrid menu-item-indent-1 ">NuGrid </a></li>
            <li><a href="${baseURL}/en/nodes/ngvs" class="nodes.ngvs menu-item-indent-1 ">NGVS </a></li>
            <li><a href="${baseURL}/en/nodes/cadc" class="nodes.cadc menu-item-indent-1 ">CADC </a></li>
            <li><a href="${baseURL}/en/nodes/uvic" class="nodes.uvic menu-item-indent-1 ">UVIC ARC </a></li>
            <li role="separator" class="divider"></li>
            <li><a href="${baseURL}/en/nodes/">Nodes </a></li>
          </ul>
        </li>

        <li class="dropdown">
          <a href="${baseURL}/en/resources" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Resources <span class="caret"></span></a>
          <ul class="dropdown-menu list-unstyled">
            <li><a href="${baseURL}/en/resources/user-guide" class="resources.user_guide menu-item-indent-1 ">User Guide </a></li>
            <li><a href="${baseURL}/en/resources/expertise" class="resources.expertise menu-item-indent-1 ">Expertise </a></li>
            <li><a href="${baseURL}/en/resources/expertise/cadc" class="resources.expertise.cadc menu-item-indent-2 ">@CADC </a></li>
            <li><a href="${baseURL}/en/resources/expertise/uvic" class="resources.expertise.uvic menu-item-indent-2 ">@UVic </a></li>
            <li><a href="${baseURL}/en/resources/services" class="resources.services menu-item-indent-1 ">Services </a></li>
            <li><a href="http://apps.canfar.net/processing/vmod/" rel="external" class="resources.services.cloud_portal menu-item-indent-2 ">Cloud Portal </a></li>
            <li><a href="${baseURL}/en/resources/services/digital-object-identifiers" class="resources.services.digital_object_identifiers menu-item-indent-2 ">Digital Object Identifiers </a></li>
            <li><a href="http://apps.canfar.net/vosui/" rel="external" class="resources.services.storage menu-item-indent-2 ">Storage </a></li>
            <li><a href="http://apps.canfar.net/storage-beta/list/" rel="external" class="resources.services.storage_beta menu-item-indent-2 ">Storage (Beta) </a></li>
            <li><a href="http://apps.canfar.net/processing/batchjobs" rel="external" class="resources.services.batch_processing menu-item-indent-2 ">Batch Processing </a></li>
            <li><a href="http://apps.canfar.net/canfar/groups" rel="external" class="resources.services.group_management menu-item-indent-2 ">Group Management </a></li>
            <li><a href="${baseURL}/en/resources/hardware" class="resources.hardware menu-item-indent-1 ">Hardware </a></li>
            <li><a href="${baseURL}/en/resources/hardware/cores" class="resources.hardware.cores menu-item-indent-2 ">Cores </a></li>
            <li><a href="${baseURL}/en/resources/hardware/disk-space" class="resources.hardware.disk_space menu-item-indent-2 ">Disk Space </a></li>
            <li role="separator" class="divider"></li>
            <li><a href="${baseURL}/en/resources/">Resources </a></li>
          </ul>
        </li>

        <li class="dropdown">
          <a href="${baseURL}/en/about" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">About <span class="caret"></span></a>
          <ul class="dropdown-menu list-unstyled">
            <li><a href="${baseURL}/en/about/terms-of-reference" class="about.terms_of_reference menu-item-indent-1 ">Terms of Reference </a></li>
            <li><a href="${baseURL}/en/about/governance" class="about.governance menu-item-indent-1 ">Governance </a></li>
            <li><a href="${baseURL}/en/about/organization" class="about.organization menu-item-indent-1 ">Organization </a></li>
            <li><a href="${baseURL}/en/about/contact" class="about.contact menu-item-indent-1 ">Contact </a></li>
            <li role="separator" class="divider"></li>
            <li><a href="${baseURL}/en/about/">About </a></li>
          </ul>
        </li>

        <li class="dropdown">
        <#include "_login.ftl">
        </li>
      </ul>
    </div> <!-- end .navbar-collapse -->
  </div> <!-- end .conatiner -->
</nav>
