{{/*
Expand the name of the chart.
*/}}
{{- define "service-mesh.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "service-mesh.labels" -}}
app.kubernetes.io/part-of: yas
app.kubernetes.io/managed-by: istio
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
{{- end }}
