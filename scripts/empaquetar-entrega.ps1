# =====================================================================
# Arma el zip de entrega del TP sin target/, .git/, .env, archivos ~$*, la carpeta documentos/,
# ni frontend/node_modules/ ni frontend/dist/ (se regeneran con npm ci / npm run build).
# Uso:  powershell -ExecutionPolicy Bypass -File scripts\empaquetar-entrega.ps1
# Resultado: TP_Telemedicina.zip en la carpeta padre del proyecto.
# =====================================================================
param(
    [string]$Destino = ""
)
$ErrorActionPreference = "Stop"

$raiz = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
if (-not $Destino) { $Destino = Join-Path (Split-Path $raiz -Parent) "TP_Telemedicina.zip" }

# Carpeta temporal donde se copia solo lo que va en la entrega
$temporal = Join-Path ([IO.Path]::GetTempPath()) ("entrega_tp_" + [Guid]::NewGuid().ToString("N"))
$carpetaTp = Join-Path $temporal "TP_Telemedicina"
New-Item -ItemType Directory -Path $carpetaTp | Out-Null

# Qué se excluye: carpetas y patrones de archivo
$carpetasExcluidas = @("target", ".git", ".idea", ".vscode", "documentos", "node_modules", "dist")
$archivosExcluidos = @(".env", "*.zip", "*.iml")

Write-Host "Copiando el proyecto (sin target/, .git/, .env, node_modules/, dist/ ni ~`$*)..."
Get-ChildItem -Path $raiz -Recurse -Force -File | ForEach-Object {
    $relativo = $_.FullName.Substring($raiz.Length + 1)
    $partes = $relativo -split '[\\/]'
    $carpetas = if ($partes.Length -gt 1) { $partes[0..($partes.Length - 2)] } else { @() }
    $enCarpetaExcluida = @($carpetas | Where-Object { $carpetasExcluidas -contains $_ }).Count -gt 0
    $nombre = $_.Name
    $archivoExcluido = @($archivosExcluidos | Where-Object { $nombre -like $_ }).Count -gt 0 -or $nombre -like '~$*'
    if (-not $enCarpetaExcluida -and -not $archivoExcluido) {
        $destinoArchivo = Join-Path $carpetaTp $relativo
        New-Item -ItemType Directory -Force -Path (Split-Path $destinoArchivo -Parent) | Out-Null
        Copy-Item -LiteralPath $_.FullName -Destination $destinoArchivo
    }
}

if (Test-Path $Destino) { Remove-Item -LiteralPath $Destino -Force }
Compress-Archive -Path $carpetaTp -DestinationPath $Destino
Remove-Item -LiteralPath $temporal -Recurse -Force

$tamanio = [Math]::Round((Get-Item -LiteralPath $Destino).Length / 1KB)
Write-Host "Listo: $Destino ($tamanio KB)" -ForegroundColor Green
