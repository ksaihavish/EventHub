$baseDir = "app/src/main/java/com/example/eventhub"
mkdir -Force "$baseDir/ui"
mkdir -Force "$baseDir/models"
mkdir -Force "$baseDir/adapters"
mkdir -Force "$baseDir/data"

# Move files
Get-ChildItem -Path $baseDir -File -Filter "*Activity.kt" | Move-Item -Destination "$baseDir/ui"
Move-Item "$baseDir/event.kt" "$baseDir/models" -ErrorAction SilentlyContinue
Move-Item "$baseDir/EventModel.kt" "$baseDir/models" -ErrorAction SilentlyContinue
Move-Item "$baseDir/ChatMessage.kt" "$baseDir/models" -ErrorAction SilentlyContinue
Move-Item "$baseDir/EventAdapter.kt" "$baseDir/adapters" -ErrorAction SilentlyContinue
Move-Item "$baseDir/ChatAdapter.kt" "$baseDir/adapters" -ErrorAction SilentlyContinue
Move-Item "$baseDir/SharedPrefManager.kt" "$baseDir/data" -ErrorAction SilentlyContinue
Move-Item "$baseDir/FirestoreManager.kt" "$baseDir/data" -ErrorAction SilentlyContinue

# Update package names and add imports
$dirs = @("ui", "models", "adapters", "data")
foreach ($dir in $dirs) {
    $files = Get-ChildItem -Path "$baseDir/$dir" -File -Filter "*.kt"
    foreach ($file in $files) {
        $content = Get-Content $file.FullName
        $newContent = @()
        foreach ($line in $content) {
            if ($line -match "^package com.example.eventhub$") {
                $newContent += "package com.example.eventhub.$dir"
                $newContent += ""
                $newContent += "import com.example.eventhub.models.*"
                $newContent += "import com.example.eventhub.adapters.*"
                $newContent += "import com.example.eventhub.data.*"
                $newContent += "import com.example.eventhub.ui.*"
                $newContent += "import com.example.eventhub.R"
                $newContent += "import com.example.eventhub.BuildConfig"
            } else {
                $newContent += $line
            }
        }
        Set-Content -Path $file.FullName -Value ($newContent -join "`n")
    }
}

# Update Manifest
$manifestPath = "app/src/main/AndroidManifest.xml"
$manifest = Get-Content $manifestPath
$manifest = $manifest -replace 'android:name="\.', 'android:name=".ui.'
Set-Content -Path $manifestPath -Value ($manifest -join "`n")

# Git Push
git add .
git commit -m "refactor: Reorganize codebase into clean packages (ui, models, adapters, data)"
git push
