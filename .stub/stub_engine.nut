// stub, collection of global functions in the engine
::Math <- {
    rand = function() {}
    min = function() {}
    minf = function() {}
    max = function() {}
    maxf = function() {}
    abs = function() {}
    absf = function() {}
    seedRandom = function() {}
    seedRandomString = function() {}
    ceil = function() {}
    floor = function() {}
    pow = function() {}
    round = function() {}
    getAngleTo = function() {}
}

::logError <- function() {}
::logInfo <- function() {}
::logDebug <- function() {}
::logWarning <- function() {}
::error <- function() {}

::split <- function() {}
::getstackinfos <- function() {}
::assert <- function() {}
::strip <- function() {}
::lstrip <- function() {}
::rstrip <- function() {}
::compilestring <- function() {}
::setroottable <- function() {}
::suspend <- function() {}
::type <- function() {}
::format <- function() {}
::getconsttable <- function() {}
::setconsttable <- function() {}
::getroottable <- function() {}
::array <- function() {}
::callee <- function() {}
::seterrorhandler <- function() {}
::newthread <- function() {}

::rand < -function() {}
::srand < -function() {}
::ceil < -function() {}
::tan < -function() {}
::pow < -function() {}
::sqrt < -function() {}
::abs < -function() {}
::atan2 < -function() {}
::asin < -function() {}
::atan < -function() {}
::cos < -function() {}
::fabs < -function() {}
::log10 < -function() {}
::floor < -function() {}
::exp < -function() {}
::acos < -function() {}
::log < -function() {}
::sin < -function() {}

::isReleaseBuild <- function() {}
::isScenarioDemo <- function() {}
::hasDLC <- function() {}
::isSteamBuild <- function() {}
::isDevmode <- function() {}
::isFirstCharacter <- function() {}
::doesBrushExist <- function() {}
::hexStringToInt <- function() {}
::new <- function() {}
::include <- function() {}
::inherit <- function() {}
::buildTextFromTemplate <- function() {}
::updateAchievement <- function() {}
::hasAchievement <- function() {}
::isKindOf <- function() {}
::createTileTransition <- function() {}
::toHash <- function() {}
::isSomethingToSee <- function() {}

::String <- {
    contains = function() {}
    replace = function() {}
    remove = function() {}
}

::Tactical <- {
    spawnEntity = function() {}
    tileToWorld = function() {}
    getTile = function() {}
    queryTilesInRange = function() {}
    spawnParticleEffect = function() {}
    setBlockedTileHighlightsVisibility = function() {}
    getEntityByID = function() {}
    getTileSquare = function() {}
    spawnHeadEffect = function() {}
    render = function() {}
    getWeather = function() {}
    setVisible = function() {}
    getSurvivorRoster = function() {}
    setTransitions = function() {}
    fillVisibility = function() {}
    isVisible = function() {}
    resizeScene = function() {}
    getCamera = function() {}
    setAmbientColor = function() {}
    getNavigator = function() {}
    clearHeat = function() {}
    isActive = function() {}
    loadResources = function() {}
    addEntityToMap = function() {}
    isValidTile = function() {}
    getHighlighter = function() {}
    getMapSize = function() {}
    screenToTile = function() {}
    spawnAttackEffect = function() {}
    queryActorsInRange = function() {}
    clearBlockedTileHighlights = function() {}
    spawnProjectileEffect = function() {}
    update = function() {}
    isValidTileSquare = function() {}
    worldToTile = function() {}
    calculateTacticalValuesForTerrain = function() {}
    addResource = function() {}
    setActive = function() {}
    clearVisibility = function() {}
    clearScene = function() {}
    spawnSpriteEffect = function() {}
    getTemporaryRoster = function() {}
    getRetreatRoster = function() {}
    getCasualtyRoster = function() {}
    getShaker = function() {}
    fillHeat = function() {}
    spawnIconEffect = function() {}
    spawnPoolEffect = function() {}
    createBlockedTileHighlights = function() {}
},


    ::Sound = {
        setAmbience = function() {}
        stopAmbience = function() {}
        play = function() {}
        update = function() {}
        setAmbienceVolume = function() {}
    },

    ::World = {
        spawnEntity = function() {}
        getEntityAtPos = function() {}
        getTile = function() {}
        spawnLocation = function() {}
        spawnParticleEffect = function() {}
        tileToWorld = function() {}
        canLoad = function() {}
        load = function() {}
        presave = function() {}
        save = function() {}
        getSpeedMult = function() {}
        setSpeedMult = function() {}
        queryTilesInRange = function() {}
        prepareRender = function() {}
        getEntityByID = function() {}
        findNextTileOfType = function() {}
        setVisible = function() {}
        resizeScene = function() {}
        getPlayerVisionRadius = function() {}
        setAmbientColor = function() {}
        getNavigator = function() {}
        setOnBeforeSaveCallback = function() {}
        isValidTile = function() {}
        setOnLoadCallback = function() {}
        screenToTile = function() {}
        clearTiles = function() {}
        update = function() {}
        deleteRoster = function() {}
        isValidTileSquare = function() {}
        updateTilesWithHeat = function() {}
        setOnBeforeLoadCallback = function() {}
        setFogOfWar = function() {}
        isDaytime = function() {}
        getAllRosters = function() {}
        getAllEntitiesAndOneLocationAtPos = function() {}
        getTileSquare = function() {}
        findNextRegion = function() {}
        render = function() {}
        getWeather = function() {}
        spawnRegionText = function() {}
        getTemporaryRoster = function() {}
        getDirection8FromTo = function() {}
        setOnSaveCallback = function() {}
        setPlayerVisionRadius = function() {}
        getAllEntitiesVisibleAtPos = function() {}
        move = function() {}
        getRoster = function() {}
        getAllTilesOfRegion = function() {}
        getTime = function() {}
        getAllEntitiesAtPos = function() {}
        getEntityAtTile = function() {}
        getGlobalVisibilityMult = function() {}
        worldToTile = function() {}
        uncoverFogOfWar = function() {}
        getAllFootprintsAtPos = function() {}
        getCamera = function() {}
        spawnWaveSprite = function() {}
        getNumOfTilesWithType = function() {}
        getGuestRoster = function() {}
        isUsingFogOfWar = function() {}
        isVisible = function() {}
        getMapSize = function() {}
        setPlayerPos = function() {}
        spawnFootprint = function() {}
        createRoster = function() {}
        getPlayerPos = function() {}
        getPlayerRoster = function() {}
        getPlayerEntity = function() {}
        clearScene = function() {}
    }

::IO = {
    enumerateFiles = function() {}
    scriptHashByFilename = function() {}
    scriptFilenameByHash = function() {}
}

::Time = {
    getFPS = function() {},
    getDelta = function() {}, // difference between last 2 frames
    getVirtualTimeF = function() {}, // returns game time since the start of the save
    getExactTime = function() {}, // returns precise time since game launch
    setRound = function() {},
    setVirtualSpeed = function() {},
    clearEvents = function() {},
    getVirtualTime = function() {},
    hasEventScheduled = function() {},
    setVirtualTime = function() {},
    getVirtualSpeed = function() {},
    getRound = function() {},
    getVirtualDelta = function() {},
    getRealTime = function() {},
    getFrame = function() {},
    getRealTimeF = function() {},
    scheduleEvent = function() {},
}

::Music = {
    setTrackList = function() {},
    isPlaying = function() {}
},

    ::Settings = {
        load = function() {},
        save = function() {},
        queryResolutionByIndex = function() {},
        getTempGameplaySettings = function() {},
        getControlSettings = function() {},
        setHardwareSound = function() {},
        getGameplaySettings = function() {},
        isHardwareSound = function() {},
        queryResolutions = function() {},
        setSoundVolume = function() {},
        setVideoMode = function() {},
        queryResolutionIndexByVideoMode = function() {},
        getVideoMode = function() {},
        saveWithCustomVideoMode = function() {},
        getSoundVolume = function() {}
    },

    ::UI = {
        getCursorOffsets = function() {},
        setCursorHardware = function() {},
        getCursorSize = function() {},
        setCursorPosition = function() {},
        connect = function() {},
        isMouseOver = function() {},
        disconnect = function() {},
        getCursorPosition = function() {},
        setCursor = function() {},
        isCursorHardware = function() {}
    }

::GameInfo = {
    getBuildName = function() {}
    getRevisionNumber = function() {}
    getBuildDate = function() {}
    getCompany = function() {}
    getVersionNumber = function() {}
    getVersionName = function() {}
}

::TimeUnit = {
    Real = 0,
    Virtual = 1,
    Rounds = 2
}