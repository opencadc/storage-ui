{{/*
Resolve the Kubernetes Secret name containing the OIDC client secret.

Preferred configuration (current):

  deployment.storageUI.oidc.clientSecret:
    secretName: my-oidc-client
    secretKey: clientSecret   # optional, defaults to "clientSecret"

Backward compatible configuration (deprecated):

  deployment.storageUI.oidc.existingSecretName: my-oidc-client

In the deprecated form the key is assumed to be "clientSecret".
*/}}
{{- define "storageui.oidc.clientSecret.secretName" -}}
{{- $oidc := .Values.deployment.storageUI.oidc | default dict }}
{{- $cs := $oidc.clientSecret | default dict }}
{{- if and $cs $cs.secretName }}
{{- $cs.secretName }}
{{- else if $oidc.existingSecretName }}
{{- $oidc.existingSecretName }}
{{- end }}
{{- end }}

{{/*
Resolve the key within the OIDC client Secret which holds the client secret value.

When using the clientSecret object, the key defaults to "clientSecret" if not specified.
When using the deprecated existingSecretName, the key is always "clientSecret".
*/}}
{{- define "storageui.oidc.clientSecret.secretKey" -}}
{{- $oidc := .Values.deployment.storageUI.oidc | default dict }}
{{- $cs := $oidc.clientSecret | default dict }}
{{- if and $cs $cs.secretName }}
{{- $cs.secretKey | default "clientSecret" }}
{{- else if $oidc.existingSecretName }}
{{- "clientSecret" -}}
{{- end }}
{{- end }}
