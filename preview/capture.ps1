param(
	[string]$Output = "preview/out/live-title.png",
	[int]$TimeoutSeconds = 240,
	[int]$SettleSeconds = 30
)

Add-Type -AssemblyName System.Drawing

Add-Type @"
using System;
using System.Runtime.InteropServices;
public class Win32 {
	[DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr hWnd);
	[DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
	[DllImport("user32.dll")] public static extern bool GetWindowRect(IntPtr hWnd, out RECT lpRect);
	public struct RECT { public int Left, Top, Right, Bottom; }
}
"@

$deadline = (Get-Date).AddSeconds($TimeoutSeconds)
$process = $null

while ((Get-Date) -lt $deadline) {
	$process = Get-Process javaw, java -ErrorAction SilentlyContinue |
		Where-Object { $_.MainWindowTitle -like "*Minecraft*" } |
		Select-Object -First 1

	if ($process) { break }
	Start-Sleep -Seconds 2
}

if (-not $process) {
	Write-Output "No Minecraft window found within $TimeoutSeconds seconds."
	exit 1
}

Write-Output "Window: $($process.MainWindowTitle) ($($process.Id))"
Start-Sleep -Seconds $SettleSeconds

[Win32]::ShowWindow($process.MainWindowHandle, 9) | Out-Null
[Win32]::SetForegroundWindow($process.MainWindowHandle) | Out-Null
Start-Sleep -Seconds 2

$rect = New-Object Win32+RECT
[Win32]::GetWindowRect($process.MainWindowHandle, [ref]$rect) | Out-Null

$width = $rect.Right - $rect.Left
$height = $rect.Bottom - $rect.Top
Write-Output "Rect: $($rect.Left),$($rect.Top) ${width}x${height}"

$bitmap = New-Object System.Drawing.Bitmap($width, $height)
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.CopyFromScreen($rect.Left, $rect.Top, 0, 0, $bitmap.Size)
$graphics.Dispose()

$directory = Split-Path -Parent $Output
if ($directory) { New-Item -ItemType Directory -Force -Path $directory | Out-Null }

$bitmap.Save((Join-Path (Get-Location) $Output), [System.Drawing.Imaging.ImageFormat]::Png)
$bitmap.Dispose()
Write-Output "Saved $Output"
