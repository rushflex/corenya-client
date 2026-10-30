<#
    Corenya Vanilla - Resource Pack generator
    ------------------------------------------------------------
    Bu script tum PNG ve JSON dosyalarini sifirdan uretir.
    Elle cizilmis 8x8 ikon seti + 5x7 gradient font.

    Kullanim:
        powershell -ExecutionPolicy Bypass -File tools\Generate-Pack.ps1
#>
[CmdletBinding()]
param(
    [string]$Out
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

if (-not $Out) { $Out = Join-Path (Split-Path -Parent $MyInvocation.MyCommand.Path) '..\pack' }

$Out = [System.IO.Path]::GetFullPath($Out)

# ---------------------------------------------------------------- palettes
# Her ikon 4 ton kullanir: 1 = outline, 2 = shade, 3 = base, 4 = light
$Palettes = @{
    globe    = @('#06131F', '#1B6FA8', '#20A0E0', '#4FE07A')
    star     = @('#6B4A00', '#A8760A', '#FFC32B', '#FFF07A')
    crown    = @('#6B4A00', '#C08A0E', '#FFC72E', '#FFF08A')
    gem      = @('#0A4A5A', '#1F94B8', '#35D6F5', '#B9FAFF')
    skull    = @('#3A3A46', '#9AA0AA', '#E6E9EE', '#FFFFFF')
    swords   = @('#2A2E36', '#7E8794', '#C9D2DE', '#FFFFFF')
    coin     = @('#5C3E00', '#C79A0E', '#FFD447', '#FFF3A8')
    calendar = @('#4A1A1A', '#C0392B', '#F2F2F2', '#FFFFFF')
    users    = @('#0A3A4A', '#1F94B8', '#3FE0C0', '#BFFFF4')
    vote     = @('#0C3A12', '#2E9E3E', '#56E36B', '#B8FFC2')
    clock    = @('#1B2230', '#6C7A8C', '#E6E9EE', '#FFFFFF')
    heart    = @('#4A0A14', '#B3122E', '#FF3355', '#FF9AAA')
    shield   = @('#121A2E', '#2B4C8C', '#3B6FD4', '#9BC2FF')
    bolt     = @('#5C4500', '#E0A800', '#FFD93D', '#FFF8B0')
    cross    = @('#4A0A14', '#B3122E', '#FF3355', '#FF8FA0')
    arrow    = @('#12303A', '#2A6C7C', '#45C4D8', '#B9F4FF')
}

# ---------------------------------------------------------------- icon art (8x8)
$Icons = [ordered]@{
    globe = @(
        '..1111..'
        '.133331.'
        '13333331'
        '13343331'
        '13344331'
        '13333331'
        '.133331.'
        '..1111..'
    )
    star = @(
        '...11...'
        '...44...'
        '11144111'
        '14444441'
        '.144441.'
        '..1441..'
        '..1441..'
        '.14..41.'
    )
    crown = @(
        '1..11..1'
        '14.44.41'
        '14444441'
        '14144141'
        '14444441'
        '14444441'
        '11111111'
        '........'
    )
    gem = @(
        '..1111..'
        '.144441.'
        '14444441'
        '.144441.'
        '.144441.'
        '..1441..'
        '...11...'
        '........'
    )
    skull = @(
        '..1111..'
        '.144441.'
        '14444441'
        '14144141'
        '14144141'
        '14444441'
        '.1441441'
        '..1111..'
    )
    swords = @(
        '.4....4.'
        '.44..44.'
        '..4444..'
        '...44...'
        '...44...'
        '..4444..'
        '.44..44.'
        '.4....4.'
    )
    coin = @(
        '..1111..'
        '.144441.'
        '14444441'
        '14414441'
        '14444441'
        '14414441'
        '.144441.'
        '..1111..'
    )
    calendar = @(
        '11111111'
        '12222221'
        '14444441'
        '14141441'
        '14444441'
        '14141441'
        '14444441'
        '11111111'
    )
    users = @(
        '..111...'
        '.14441..'
        '.14441..'
        '..111...'
        '.14441..'
        '1444441.'
        '1444441.'
        '.11111..'
    )
    vote = @(
        '......11'
        '.....144'
        '....144.'
        '...144..'
        '1..144..'
        '.1.44...'
        '..144...'
        '...1....'
    )
    clock = @(
        '..1111..'
        '.144441.'
        '14444441'
        '14414441'
        '14411441'
        '14444441'
        '.144441.'
        '..1111..'
    )
    heart = @(
        '........'
        '.11..11.'
        '14444441'
        '14444441'
        '14444441'
        '.144441.'
        '..1441..'
        '...11...'
    )
    shield = @(
        '11111111'
        '14444441'
        '14444441'
        '14444441'
        '.144441.'
        '..1441..'
        '...11...'
        '........'
    )
    bolt = @(
        '...1111.'
        '..1441..'
        '..1441..'
        '.1441...'
        '1444441.'
        '..1441..'
        '.1441...'
        '.11.....'
    )
    cross = @(
        '.1....1.'
        '.14..41.'
        '..1441..'
        '...11...'
        '...11...'
        '..1441..'
        '.14..41.'
        '.1....1.'
    )
    arrow = @(
        '........'
        '...1....'
        '....1...'
        '1111111.'
        '....1...'
        '...1....'
        '........'
        '........'
    )
}

# ---------------------------------------------------------------- 5x7 glyphs
$Glyphs = @{
    'A' = '.###.|#...#|#...#|#####|#...#|#...#|#...#'
    'B' = '####.|#...#|#...#|####.|#...#|#...#|####.'
    'C' = '.####|#....|#....|#....|#....|#....|.####'
    'D' = '####.|#...#|#...#|#...#|#...#|#...#|####.'
    'E' = '#####|#....|#....|####.|#....|#....|#####'
    'F' = '#####|#....|#....|####.|#....|#....|#....'
    'G' = '.###.|#...#|#....|#.###|#...#|#...#|.###.'
    'H' = '#...#|#...#|#...#|#####|#...#|#...#|#...#'
    'I' = '#####|..#..|..#..|..#..|..#..|..#..|#####'
    'J' = '..###|...#.|...#.|...#.|...#.|#..#.|.##..'
    'K' = '#...#|#..#.|#.#..|##...|#.#..|#..#.|#...#'
    'L' = '#....|#....|#....|#....|#....|#....|#####'
    'M' = '#...#|##.##|#.#.#|#...#|#...#|#...#|#...#'
    'N' = '#...#|##..#|#.#.#|#..##|#...#|#...#|#...#'
    'O' = '.###.|#...#|#...#|#...#|#...#|#...#|.###.'
    'P' = '####.|#...#|#...#|####.|#....|#....|#....'
    'Q' = '.###.|#...#|#...#|#...#|#.#.#|#..#.|.##.#'
    'R' = '####.|#...#|#...#|####.|#.#..|#..#.|#...#'
    'S' = '.####|#....|#....|.###.|....#|....#|####.'
    'T' = '#####|..#..|..#..|..#..|..#..|..#..|..#..'
    'U' = '#...#|#...#|#...#|#...#|#...#|#...#|.###.'
    'V' = '#...#|#...#|#...#|#...#|#...#|.#.#.|..#..'
    'W' = '#...#|#...#|#...#|#.#.#|#.#.#|##.##|#...#'
    'X' = '#...#|#...#|.#.#.|..#..|.#.#.|#...#|#...#'
    'Y' = '#...#|#...#|.#.#.|..#..|..#..|..#..|..#..'
    'Z' = '#####|....#|...#.|..#..|.#...|#....|#####'
    '0' = '.###.|#...#|#..##|#.#.#|##..#|#...#|.###.'
    '1' = '..#..|.##..|..#..|..#..|..#..|..#..|.###.'
    '2' = '.###.|#...#|....#|...#.|..#..|.#...|#####'
    '3' = '#####|...#.|..#..|...#.|....#|#...#|.###.'
    '4' = '...#.|..##.|.#.#.|#..#.|#####|...#.|...#.'
    '5' = '#####|#....|####.|....#|....#|#...#|.###.'
    '6' = '..##.|.#...|#....|####.|#...#|#...#|.###.'
    '7' = '#####|....#|...#.|..#..|.#...|.#...|.#...'
    '8' = '.###.|#...#|#...#|.###.|#...#|#...#|.###.'
    '9' = '.###.|#...#|#...#|.####|....#|...#.|.##..'
    '.' = '.....|.....|.....|.....|.....|.##..|.##..'
    ',' = '.....|.....|.....|.....|.##..|.##..|.#...'
    ':' = '.....|.##..|.##..|.....|.##..|.##..|.....'
    ';' = '.....|.##..|.##..|.....|.##..|.##..|.#...'
    '-' = '.....|.....|.....|#####|.....|.....|.....'
    '_' = '.....|.....|.....|.....|.....|.....|#####'
    '/' = '....#|...#.|...#.|..#..|.#...|.#...|#....'
    '\' = '#....|.#...|.#...|..#..|...#.|...#.|....#'
    '+' = '.....|..#..|..#..|#####|..#..|..#..|.....'
    '*' = '.....|#.#.#|.###.|#####|.###.|#.#.#|.....'
    '$' = '..#..|.####|#.#..|.###.|..#.#|####.|..#..'
    '%' = '##..#|##.#.|...#.|..#..|.#...|.#.##|#..##'
    '#' = '.#.#.|#####|.#.#.|.#.#.|#####|.#.#.|.....'
    '!' = '..#..|..#..|..#..|..#..|..#..|.....|..#..'
    '?' = '.###.|#...#|....#|...#.|..#..|.....|..#..'
    '&' = '.##..|#..#.|#.#..|.#...|#.#.#|#..#.|.##.#'
    '(' = '...#.|..#..|.#...|.#...|.#...|..#..|...#.'
    ')' = '.#...|..#..|...#.|...#.|...#.|..#..|.#...'
    '[' = '.###.|.#...|.#...|.#...|.#...|.#...|.###.'
    ']' = '.###.|...#.|...#.|...#.|...#.|...#.|.###.'
    '<' = '...#.|..#..|.#...|#....|.#...|..#..|...#.'
    '>' = '.#...|..#..|...#.|....#|...#.|..#..|.#...'
    '=' = '.....|.....|#####|.....|#####|.....|.....'
    "'" = '..#..|..#..|.....|.....|.....|.....|.....'
    '"' = '.#.#.|.#.#.|.....|.....|.....|.....|.....'
    '|' = '..#..|..#..|..#..|..#..|..#..|..#..|..#..'
    '@' = '.###.|#...#|#.###|#.#.#|#.###|#....|.###.'
    ' ' = '.....|.....|.....|.....|.....|.....|.....'
}

# Turkce harfleri en yakin karsiligiyla esle (glyph tekrari olusturmayiz)
$TurkishMap = @{
    [char]0x00C7 = 'C'; [char]0x011E = 'G'; [char]0x0130 = 'I'; [char]0x00D6 = 'O'
    [char]0x015E = 'S'; [char]0x00DC = 'U'; [char]0x0131 = 'I'; [char]0x00E7 = 'c'
    [char]0x011F = 'g'; [char]0x00F6 = 'o'; [char]0x015F = 's'; [char]0x00FC = 'u'
}

# ---------------------------------------------------------------- helpers
function New-Dir([string]$p) {
    if (-not (Test-Path -LiteralPath $p)) { New-Item -ItemType Directory -Path $p -Force | Out-Null }
}

function Hex-ToColor([string]$hex) {
    return [System.Drawing.Color]::FromArgb(255,
        [Convert]::ToInt32($hex.Substring(1, 2), 16),
        [Convert]::ToInt32($hex.Substring(3, 2), 16),
        [Convert]::ToInt32($hex.Substring(5, 2), 16))
}

function LerpColor([System.Drawing.Color]$a, [System.Drawing.Color]$b, [double]$t) {
    return [System.Drawing.Color]::FromArgb(
        255,
        [int][Math]::Round($a.R + ($b.R - $a.R) * $t),
        [int][Math]::Round($a.G + ($b.G - $a.G) * $t),
        [int][Math]::Round($a.B + ($b.B - $a.B) * $t))
}

function Save-Png($bitmap, [string]$path) {
    New-Dir (Split-Path -Parent $path)
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
}

function New-Preview([System.Drawing.Bitmap]$src, [string]$path, [int]$scale, [string]$bg = '#101018') {
    $pad = 8
    $w = $src.Width * $scale + $pad * 2
    $h = $src.Height * $scale + $pad * 2
    $out = New-Object System.Drawing.Bitmap($w, $h)
    $g = [System.Drawing.Graphics]::FromImage($out)
    $g.Clear((Hex-ToColor $bg))
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
    $g.DrawImage($src, $pad, $pad, $src.Width * $scale, $src.Height * $scale)
    $g.Dispose()
    Save-Png $out $path
    $out.Dispose()
}

# ---------------------------------------------------------------- icon sheet
function Build-IconSheet([string]$path, [int]$scale, [bool]$preview) {
    $count = $Icons.Count
    $bmp = New-Object System.Drawing.Bitmap(($count * 8), 8)
    $i = 0
    foreach ($name in $Icons.Keys) {
        $rows = $Icons[$name]
        if ($rows.Count -ne 8) { throw "Ikon '$name' 8 satir olmali ($($rows.Count))" }
        $pal = $Palettes[$name]
        $colors = @{
            '1' = Hex-ToColor $pal[0]
            '2' = Hex-ToColor $pal[1]
            '3' = Hex-ToColor $pal[2]
            '4' = Hex-ToColor $pal[3]
        }
        for ($y = 0; $y -lt 8; $y++) {
            $row = $rows[$y]
            if ($row.Length -ne 8) { throw "Ikon '$name' satir $y 8 karakter olmali ('$row')" }
            for ($x = 0; $x -lt 8; $x++) {
                $c = $row[$x]
                if ($c -eq '.') { continue }
                $bmp.SetPixel(($i * 8 + $x), $y, $colors[[string]$c])
            }
        }
        $i++
    }
    Save-Png $bmp $path
    if ($preview) { New-Preview $bmp (Join-Path $Out '..\preview\icons.png') $scale }
    $bmp.Dispose()
}

# ---------------------------------------------------------------- gradient font
function Build-GradientFont([string]$pngPath, [string]$charsFile, [int]$previewScale) {
    $from = Hex-ToColor '#7CF7FF'
    $to = Hex-ToColor '#C56BFF'

    $chars = New-Object System.Collections.Generic.List[string]
    # A-Z, a-z (buyuk harf cizimi), 0-9, noktalama, Turkce
    $charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 .,:;-_/\+*$%#!?&()[]<>="''|@'
    foreach ($ch in $charset.ToCharArray()) { $chars.Add([string]$ch) }
    foreach ($ch in $TurkishMap.Keys) { $chars.Add([string]$ch) }

    $perRow = 16
    $cellW = 6
    $cellH = 8
    $rows = [Math]::Ceiling($chars.Count / $perRow)
    $bmp = New-Object System.Drawing.Bitmap(($perRow * $cellW), ($rows * $cellH))

    $positions = @{}
    for ($i = 0; $i -lt $chars.Count; $i++) {
        $ch = $chars[$i]
        $key = $ch
        if ($TurkishMap.ContainsKey($ch[0])) { $key = [string]$TurkishMap[$ch[0]] }
        if (-not $Glyphs.ContainsKey($key)) { $key = $key.ToUpperInvariant() }
        if (-not $Glyphs.ContainsKey($key) -and $key -ne $key.ToUpperInvariant()) { $key = $key.ToUpperInvariant() }
        $art = $Glyphs[$key]
        if (-not $art) { throw "Glyph yok: '$ch'" }

        $cx = ($i % $perRow) * $cellW
        $cy = [Math]::Floor($i / $perRow) * $cellH
        $lines = $art.Split('|')
        for ($y = 0; $y -lt 7; $y++) {
            $line = $lines[$y]
            $color = LerpColor $from $to ($y / 6.0)
            for ($x = 0; $x -lt 5; $x++) {
                if ($line[$x] -eq '#') { $bmp.SetPixel(($cx + $x), ($cy + $y), $color) }
            }
        }
        # 1px alt golge (vanilla text shadow hissi)
        for ($y = 0; $y -lt 7; $y++) {
            $line = $lines[$y]
            for ($x = 0; $x -lt 5; $x++) {
                if ($line[$x] -eq '#') {
                    $px = $cx + $x + 1
                    $py = $cy + $y + 1
                    if ($px -lt $bmp.Width -and $py -lt $bmp.Height) {
                        if ($bmp.GetPixel($px, $py).A -eq 0) {
                            $c = $bmp.GetPixel($cx + $x, $cy + $y)
                            $bmp.SetPixel($px, $py, [System.Drawing.Color]::FromArgb(255,
                                [int]($c.R * 0.25), [int]($c.G * 0.25), [int]($c.B * 0.25)))
                        }
                    }
                }
            }
        }

        $escaped = ''
        foreach ($c in $ch.ToCharArray()) { $escaped += '\u{0:x4}' -f [int]$c }
        $pos = "$(( $i % $perRow ) * $cellW),$([Math]::Floor($i / $perRow) * $cellH)"
        $positions[$ch] = $pos
    }

    Save-Png $bmp $pngPath
    if ($previewScale -gt 0) { New-Preview $bmp (Join-Path $Out '..\preview\gradient.png') $previewScale }
    $bmp.Dispose()

    # JSON icin satirlara bolunmus karakter listesi
    $rowStrings = @()
    for ($r = 0; $r -lt $rows; $r++) {
        $s = ''
        for ($c = 0; $c -lt $perRow; $c++) {
            $idx = $r * $perRow + $c
            $ch = if ($idx -lt $chars.Count) { $chars[$idx] } else { ' ' }
            foreach ($cc in $ch.ToCharArray()) { $s += '\u{0:x4}' -f [int]$cc }
        }
        $rowStrings += $s
    }
    return @{ Rows = $rowStrings; Count = $chars.Count; CellW = $cellW; CellH = $cellH }
}

function Write-Utf8([string]$path, [string]$text) {
    New-Dir (Split-Path -Parent $path)
    [System.IO.File]::WriteAllText($path, $text, (New-Object System.Text.UTF8Encoding($false)))
}

# ---------------------------------------------------------------- pack.png
function Build-PackIcon([string]$path) {
    $size = 64
    $bmp = New-Object System.Drawing.Bitmap($size, $size)
    $from = Hex-ToColor '#0B1220'
    $to = Hex-ToColor '#1B2A4A'
    for ($y = 0; $y -lt $size; $y++) {
        $c = LerpColor $from $to ($y / ($size - 1))
        for ($x = 0; $x -lt $size; $x++) { $bmp.SetPixel($x, $y, $c) }
    }
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $gradient = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
        (New-Object System.Drawing.Point(0, 0)),
        (New-Object System.Drawing.Point($size, $size)),
        (Hex-ToColor '#7CF7FF'), (Hex-ToColor '#C56BFF'))
    $pen = New-Object System.Drawing.Pen($gradient, 3)
    $g.DrawRectangle($pen, 5, 5, ($size - 11), ($size - 11))
    $font = New-Object System.Drawing.Font('Consolas', 30, [System.Drawing.FontStyle]::Bold, [System.Drawing.GraphicsUnit]::Pixel)
    $fmt = New-Object System.Drawing.StringFormat
    $fmt.Alignment = [System.Drawing.StringAlignment]::Center
    $fmt.LineAlignment = [System.Drawing.StringAlignment]::Center
    $brush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
        (New-Object System.Drawing.Point(0, 14)),
        (New-Object System.Drawing.Point(0, 50)),
        (Hex-ToColor '#8AF6FF'), (Hex-ToColor '#C56BFF'))
    $g.DrawString('C', $font, $brush, (New-Object System.Drawing.RectangleF(0, 0, $size, $size)), $fmt)
    $g.Dispose()
    Save-Png $bmp $path
    $bmp.Dispose()
}

# ---------------------------------------------------------------- run
$root = $Out
$texDir = Join-Path $root 'assets\corenya\textures\font'
$fontDir = Join-Path $root 'assets\corenya\font'
$mcFontDir = Join-Path $root 'assets\minecraft\font'

Build-IconSheet (Join-Path $texDir 'icons.png') 16 $true
Build-PackIcon (Join-Path $root 'pack.png')
$gradient = Build-GradientFont (Join-Path $texDir 'gradient.png') (Join-Path $Out '..\preview\chars.txt') 6

# Ikon karakterleri: U+E000 + index
$iconChars = ''
$iconMap = [ordered]@{}
$i = 0
foreach ($name in $Icons.Keys) {
    $code = 0xE000 + $i
    $iconChars += '\u{0:x4}' -f $code
    $iconMap[$name] = ('\u{0:x4}' -f $code)
    $i++
}

$iconProvider = @"
    {
      "type": "bitmap",
      "file": "corenya:font/icons.png",
      "ascent": 7,
      "height": 8,
      "chars": [
        "$iconChars"
      ]
    }
"@

$gradRows = ($gradient.Rows | ForEach-Object { '        "' + $_ + '"' }) -join ",`n"

$gradientProvider = @"
    {
      "type": "bitmap",
      "file": "corenya:font/gradient.png",
      "ascent": 7,
      "height": 8,
      "chars": [
$gradRows
      ]
    }
"@

# corenya:icons
Write-Utf8 (Join-Path $fontDir 'icons.json') @"
{
  "providers": [
$iconProvider
  ]
}
"@

# corenya:gradient
Write-Utf8 (Join-Path $fontDir 'gradient.json') @"
{
  "providers": [
$gradientProvider
  ]
}
"@

# minecraft:default -> vanilla referanslari + bizim ikonlar
Write-Utf8 (Join-Path $mcFontDir 'default.json') @"
{
  "providers": [
    {
      "type": "reference",
      "id": "minecraft:include/space"
    },
    {
      "type": "reference",
      "id": "minecraft:include/default",
      "filter": {
        "uniform": false
      }
    },
    {
      "type": "reference",
      "id": "minecraft:include/unifont"
    },
$iconProvider
  ]
}
"@

# pack.mcmeta
Write-Utf8 (Join-Path $root 'pack.mcmeta') @"
{
  "pack": {
    "pack_format": 46,
    "supported_formats": {
      "min_inclusive": 34,
      "max_inclusive": 99
    },
    "description": "§b§lCORENYA §fVanilla §8| §7Icons & Gradient"
  }
}
"@

# ikon haritasini yaz (config uretirken / dokumantasyon icin)
$mapLines = @('# Corenya Vanilla - ikon karakter haritasi', '# Bu dosya Generate-Pack.ps1 tarafindan uretilmistir.', '')
$idx = 0
foreach ($k in $Icons.Keys) {
    $mapLines += ('{0,-10} U+{1:X4}  {2}' -f $k, (0xE000 + $idx), $iconMap[$k])
    $idx++
}
Write-Utf8 (Join-Path $root '..\preview\icon-map.txt') ($mapLines -join "`r`n")

Write-Host ''
Write-Host '  Pack uretildi:' -ForegroundColor Green
Write-Host ("  {0}" -f $root)
Write-Host ''
Write-Host '  Ikonlar (PUA):'
$i = 0
foreach ($name in $Icons.Keys) {
    Write-Host ("    U+{0:X4}  {1}" -f (0xE000 + $i), $name)
    $i++
}
Write-Host ''
Write-Host ("  Gradient font: {0} karakter, {1} satir" -f $gradient.Count, $gradient.Rows.Count)
