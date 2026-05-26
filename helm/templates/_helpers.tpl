{{/*
Resolve OIDC client Secret name when referencing an existing Kubernetes Secret.
Precedence: deployment.storageUI.oidc.existingSecret.name over deprecated existingSecretName.
*/}}
{{- define "storageui.oidc.clientSecret.secretName" -}}
{{- $oidc := .Values.deployment.storageUI.oidc | default dict }}
{{- $es := $oidc.existingSecret | default dict }}
{{- if and $es $es.name }}
{{- $es.name }}
{{- else if $oidc.existingSecretName }}
{{- $oidc.existingSecretName }}
{{- end }}
{{- end }}

{{/*
Key within that Secret holding the OIDC client secret value (projected under path clientSecret).

When using deprecated existingSecretName only, key is fixed to clientSecret.
When using existingSecret, key defaults to clientSecret if omitted.
*/}}
{{- define "storageui.oidc.clientSecret.secretKey" -}}
{{- $oidc := .Values.deployment.storageUI.oidc | default dict }}
{{- $es := $oidc.existingSecret | default dict }}
{{- if and $es $es.name }}
{{- $es.key | default "clientSecret" }}
{{- else if $oidc.existingSecretName }}
{{- "clientSecret" -}}
{{- end }}
{{- end }}
