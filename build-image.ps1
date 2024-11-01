param (
    [string]$project,
    [string]$version
)

# Check if neither project nor quarkus is specified
if (-not ($project) ) {
    Write-Host "You must specify either --project or --quarkus."
    exit
}
if (-not ($version) ) {
    Write-Host "You must specify the version"
    exit
}

# Print the command provided
if ($project -eq "--angular") {
    Write-Host "Command: --angular"
    cd flight-combination-finder-ng
    docker build -f Dockerfile -t nck974/flight-combination-finder-ng:$version .
    docker tag nck974/flight-combination-finder-ng:$version nck974/flight-combination-finder-ng:latest
    docker push nck974/flight-combination-finder-ng:$version
    cd ..
    $ImageName = "nck974/flight-combination-finder-ng"

}
if ($project -eq "--quarkus") {
    Write-Host "Building quarkus image..."
    # quarkus build
    docker build -f src/main/docker/Dockerfile.jvm -t nck974/flight-combination-finder:$version .
    docker tag nck974/flight-combination-finder:$version nck974/flight-combination-finder:latest
    docker push nck974/flight-combination-finder:$version
    docker push nck974/flight-combination-finder:latest
    $ImageName = "nck974/flight-combination-finder"
}

# Check if the image exists
# Run the docker images command and filter the output
$imageExists = docker images $ImageName | Select-String -Pattern "^$ImageName\s+$Tag\s+"

if ($imageExists) {
    Write-Host "The image $ImageName with tag $version exists."
} else {
    Write-Host "The image $ImageName with tag $Tag does not exist."
}


