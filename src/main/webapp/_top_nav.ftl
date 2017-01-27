<#assign baseURL = 'http://www.canfar.net'>

<nav id="top_nav" class="navbar navbar-default navbar-fixed-top">
  <div class="container">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed"
              data-toggle="collapse" data-target="#canfar_navbar_collapse"
              aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
      </button>
      <a class="navbar-brand" href="${baseURL}"><img
          src="${baseURL}/css/images/logo.png"/></a>
    </div>

    <!-- RT 73828: temporary message for early versions -->
    <div class="early-access text-warning">Early access with limited functionality</div>

    <div id="canfar_navbar_collapse" class="collapse navbar-collapse">

      <ul id="navbar_list" class="nav navbar-nav pull-right">
        <!-- Uncomment this to have a language switcher. -->
        <!--<li class="dropdown">-->
        <!--<a href="/en" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><span class="text-success">en</span> <span class="caret"></span></a>-->
        <!--<ul class="dropdown-menu list-unstyled">-->
        <!---->
        <!--</ul>-->
        <!--</li>-->
        <li class="dropdown">
          <a href="${baseURL}/en/nodes" class="dropdown-toggle"
             data-toggle="dropdown" role="button" aria-haspopup="true"
             aria-expanded="false">Nodes <span class="caret"></span></a>
          <ul class="dropdown-menu list-unstyled">
            <li><a href="${baseURL}/en/nodes/cadc"
                   class="nodes.cadc menu-item-indent-1 ">CADC </a></li>
            <li><a href="${baseURL}/en/nodes/euclid"
                   class="nodes.euclid menu-item-indent-1 ">Euclid </a></li>
            <li><a href="${baseURL}/en/nodes/inaf-oat"
                   class="nodes.inaf-oat menu-item-indent-1 ">INAF-OAT </a></li>
            <li><a href="${baseURL}/en/nodes/jcmt-gbs"
                   class="nodes.jcmt_gbs menu-item-indent-1 ">JCMT GBS </a></li>
            <li><a href="${baseURL}/en/nodes/jwst"
                   class="nodes.jwst menu-item-indent-1 ">JWST </a></li>
            <li><a href="${baseURL}/en/nodes/ngvs"
                   class="nodes.ngvs menu-item-indent-1 ">NGVS </a></li>
            <li><a href="${baseURL}/en/nodes/nugrid"
                   class="nodes.nugrid menu-item-indent-1 ">NuGrid </a></li>
            <li><a href="${baseURL}/en/nodes/taos-ii"
                   class="nodes.taos_ii menu-item-indent-1 ">TAOS-II </a></li>
            <li><a href="${baseURL}/en/nodes/uvic"
                   class="nodes.uvic menu-item-indent-1 ">UVIC ARC </a></li>
            <li><a href="${baseURL}/en/nodes/vlass"
                   class="nodes.vlass menu-item-indent-1 ">VLASS </a></li>
            <li role="separator" class="divider"></li>
            <li><a href="${baseURL}/en/nodes/">Nodes </a></li>
          </ul>
        </li>

        <li class="dropdown">
          <a href="${baseURL}/en/resources" class="dropdown-toggle"
             data-toggle="dropdown" role="button" aria-haspopup="true"
             aria-expanded="false">Resources <span class="caret"></span></a>
          <ul class="dropdown-menu list-unstyled">
            <li><a href="${baseURL}/en/resources/user-guide"
                   class="resources.user_guide menu-item-indent-1 ">User
              Guide </a></li>
            <li><a href="${baseURL}/en/resources/expertise"
                   class="resources.expertise menu-item-indent-1 ">Expertise </a>
            </li>
            <li><a href="${baseURL}/en/resources/services/"
                   class="resources.services menu-item-indent-1 ">Services </a>
            </li>
            <li><a href="${baseURL}/en/resources/hardware"
                   class="resources.hardware menu-item-indent-1 ">Hardware </a>
            </li>
            <li role="separator" class="divider"></li>
            <li><a href="${baseURL}/en/resources/">Resources </a></li>
          </ul>
        </li>

        <li class="dropdown">
          <a href="${baseURL}/en/about" class="dropdown-toggle"
             data-toggle="dropdown" role="button" aria-haspopup="true"
             aria-expanded="false">About <span class="caret"></span></a>
          <ul class="dropdown-menu list-unstyled">
            <li><a href="${baseURL}/en/about/terms-of-reference"
                   class="about.terms_of_reference menu-item-indent-1 ">Terms of
              Reference </a></li>
            <li><a href="${baseURL}/en/about/governance"
                   class="about.governance menu-item-indent-1 ">Governance </a>
            </li>
            <li><a href="${baseURL}/en/about/organization"
                   class="about.organization menu-item-indent-1 ">Organization </a>
            </li>
            <li><a href="${baseURL}/en/about/partners"
                   class="about.partners menu-item-indent-1 ">Partners </a></li>
            <li><a href="${baseURL}/en/about/contact"
                   class="about.contact menu-item-indent-1 ">Contact </a></li>
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
