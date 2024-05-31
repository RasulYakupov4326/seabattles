import kotlin.random.Random

val xCoordinates = listOf('А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ж', 'З', 'И', 'К')

fun coordinateTranslation(rawCoordinates: String): Pair<Int, Int> {
    var result = Pair(-1, -1)
    if (rawCoordinates.length < 2) return result
    val letter = rawCoordinates[0]
    val number = rawCoordinates.substring(1).toIntOrNull()

    if (number != null) {
        val x = xCoordinates.indexOf(letter)
        val y = number - 1

        if (x in 0..9 && y in 0..9) {
            result = Pair(y, x)
        }
    }
    return result
}

fun fleetConstructor(field: Array<Array<Char>>, start: Pair<Int, Int>, end: Pair<Int, Int>, size: Int): Boolean {
    val (y1, x1) = start
    val (y2, x2) = end

    if (x1 != x2 && y1 != y2) return false

    val length = if (x1 == x2) Math.abs(y2 - y1) + 1 else Math.abs(x2 - x1) + 1

    if (length != size) return false

    for (i in 0 until length) {
        val x = if (x1 == x2) x1 else x1 + i * Integer.signum(x2 - x1)
        val y = if (y1 == y2) y1 else y1 + i * Integer.signum(y2 - y1)

        if (y !in 0..9 || x !in 0..9 || field[y][x] != '.') {
            return false
        }

        for (dy in -1..1) {
            for (dx in -1..1) {
                val ny = y + dy
                val nx = x + dx
                if (ny in 0..9 && nx in 0..9 && field[ny][nx] == '#') {
                    return false
                }
            }
        }
    }

    for (i in 0 until length) {
        val x = if (x1 == x2) x1 else x1 + i * Integer.signum(x2 - x1)
        val y = if (y1 == y2) y1 else y1 + i * Integer.signum(y2 - y1)
        field[y][x] = '#'
    }

    return true
}

fun gameMode() {
    var gameContinues = true
    while (gameContinues) {
        println("Выберите режим:\n1)Для одного игрока\n2)Для двух игроков\n3)Завершить программу")
        val gameModeSelection = readLine()?.toIntOrNull()
        when (gameModeSelection) {
            1 -> soloGameMode()
            2 -> twoPlayerGameMode()
            3 -> {
                println("Завершение программы")
                gameContinues = false
            }
            else -> println("Данного варианта не существует")
        }
    }
}

fun soloGameMode() {
    println("Введите имя игрока:")
    val playerName = readLine() ?: "Игрок"
    val playerField = Array(10) { Array(10) { '.' } }
    val playerVisibleField = Array(10) { Array(10) { '.' } }
    val enemyField = Array(10) { Array(10) { '.' } }
    val enemyVisibleField = Array(10) { Array(10) { '.' } }

    println("Как вы хотите расставить корабли:\n1)Случайно\n2)Расставить самому")
    val placementMode = readLine()?.toIntOrNull()
    if (placementMode == 1) {
        randomFleetPlacement(playerField)
    } else {
        println("$playerName, расставьте свои корабли:")
        fleetPlacement(playerField)
    }
    randomFleetPlacement(enemyField)

    game(playerName, "Компьютер", playerField, playerVisibleField, enemyField, enemyVisibleField, true)
}

fun twoPlayerGameMode() {
    println("Игрок 1, введите имя:")
    val player1Name = readLine() ?: "Игрок 1"
    println("Игрок 2, введите имя:")
    val player2Name = readLine() ?: "Игрок 2"

    val player1Field = Array(10) { Array(10) { '.' } }
    val player1VisibleField = Array(10) { Array(10) { '.' } }
    val player2Field = Array(10) { Array(10) { '.' } }
    val player2VisibleField = Array(10) { Array(10) { '.' } }

    println("Игрок 1, как вы хотите расставить корабли:\n1)Случайно\n2)Расставить самому")
    val placementMode1 = readLine()?.toIntOrNull()
    if (placementMode1 == 1) {
        randomFleetPlacement(player1Field)
    } else {
        println("$player1Name, расставьте свои корабли:")
        fleetPlacement(player1Field)
    }

    println("Игрок 2, как вы хотите расставить корабли:\n1)Случайно\n2)Расставить самому")
    val placementMode2 = readLine()?.toIntOrNull()
    if (placementMode2 == 1) {
        randomFleetPlacement(player2Field)
    } else {
        println("$player2Name, расставьте свои корабли:")
        fleetPlacement(player2Field)
    }

    game(player1Name, player2Name, player1Field, player1VisibleField, player2Field, player2VisibleField, false)
}

fun fleetPlacement(field: Array<Array<Char>>) {
    val ships = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1)
    for (size in ships) {
        var placed = false
        while (!placed) {
            printField(field)
            println("Введите координаты начала и конца корабля размером $size (Пример: А3 А6 для вертикального или А3 Д3 для горизонтального):")
            val input = readLine()?.split(" ")
            if (input != null && input.size == 2) {
                val start = coordinateTranslation(input[0])
                val end = coordinateTranslation(input[1])

                if (start.first != -1 && end.first != -1) {
                    placed = fleetConstructor(field, start, end, size)
                    if (!placed) {
                        println("Невозможно разместить корабль по данным координатам или неверная длина корабля. Попробуйте снова.")
                    }
                } else {
                    println("Некорректный ввод. Пожалуйста, введите координаты в правильном формате.")
                }
            } else {
                println("Некорректный ввод. Пожалуйста, введите координаты в правильном формате.")
            }
        }
    }
}

fun randomFleetPlacement(field: Array<Array<Char>>) {
    val ships = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1)
    val random = Random.Default

    for (size in ships) {
        var placed = false
        while (!placed) {
            val y = random.nextInt(10)
            val x = random.nextInt(10)
            val direction = random.nextInt(2)

            val start = Pair(y, x)
            val end = if (direction == 0) {
                Pair(y, x + size - 1)
            } else {
                Pair(y + size - 1, x)
            }

            if (start.first in 0..9 && start.second in 0..9 && end.first in 0..9 && end.second in 0..9) {
                placed = fleetConstructor(field, start, end, size)
            }
        }
    }
}

fun printField(field: Array<Array<Char>>) {
    println("  " + xCoordinates.joinToString(" "))
    for (i in field.indices) {
        println("${i + 1} " + field[i].joinToString(" "))
    }
}

fun game(player1Name: String, player2Name: String, playerField: Array<Array<Char>>, playerVisibleField: Array<Array<Char>>, enemyField: Array<Array<Char>>, enemyVisibleField: Array<Array<Char>>, solo: Boolean) {
    var playerUnits = 20
    var enemyUnits = 20
    val random = Random.Default
    val playerShots = mutableSetOf<Pair<Int, Int>>()
    val enemyShots = mutableSetOf<Pair<Int, Int>>()

    while (playerUnits > 0 && enemyUnits > 0) {
        printField(enemyVisibleField)
        println("$player1Name, введите координату для выстрела (Пример: А3):")
        val input = readLine()
        val (y, x) = coordinateTranslation(input ?: "")
        if (y != -1 && x != -1 && (y to x) !in playerShots) {
            playerShots.add(y to x)
            if (enemyField[y][x] == '#') {
                enemyVisibleField[y][x] = 'x'
                enemyField[y][x] = 'x'
                enemyUnits--
            } else {
                enemyVisibleField[y][x] = 'o'
                enemyField[y][x] = 'o'
            }
        } else {
            println("Некорректная или повторная координата. Попробуйте снова.")
            continue
        }

        if (solo && enemyUnits > 0) {
            var enemyShot = false
            while (!enemyShot) {
                val ey = random.nextInt(10)
                val ex = random.nextInt(10)
                if ((ey to ex) !in enemyShots) {
                    enemyShots.add(ey to ex)
                    if (playerField[ey][ex] == '#') {
                        playerVisibleField[ey][ex] = 'x'
                        playerField[ey][ex] = 'x'
                        playerUnits--
                    } else {
                        playerVisibleField[ey][ex] = 'o'
                        playerField[ey][ex] = 'o'
                    }
                    enemyShot = true
                }
            }
        }

        if (!solo && playerUnits > 0) {
            printField(playerVisibleField)
            println("$player2Name, введите координату для выстрела (Пример: А3):")
            val input2 = readLine()
            val (y2, x2) = coordinateTranslation(input2 ?: "")
            if (y2 != -1 && x2 != -1 && (y2 to x2) !in enemyShots) {
                enemyShots.add(y2 to x2)
                if (playerField[y2][x2] == '#') {
                    playerVisibleField[y2][x2] = 'x'
                    playerField[y2][x2] = 'x'
                    playerUnits--
                } else {
                    playerVisibleField[y2][x2] = 'o'
                    playerField[y2][x2] = 'o'
                }
            } else {
                println("Некорректная или повторная координата. Попробуйте снова.")
                continue
            }
        }
    }

    if (playerUnits == 0) {
        println("$player2Name выиграл!")
    } else if (enemyUnits == 0) {
        println("$player1Name выиграл!")
    }
}

fun main() {
    gameMode()
}