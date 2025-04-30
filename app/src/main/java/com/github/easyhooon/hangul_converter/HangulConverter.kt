// HangulConverter.kt
package com.github.easyhooon.hangul_converter

object HangulConverter {
    /**
     * 영어 키보드로 친 한글을 실제 한글로 변환
     * @param eng 변환할 영어 텍스트
     * @return 변환된 한글 텍스트
     */
    fun engToKor(eng: String): String {
        // 초성 인덱스 매핑 (가나다 순서)
        val choSungIndex = mapOf(
            'r' to 0, 'R' to 1, 's' to 2, 'e' to 3, 'E' to 4,
            'f' to 5, 'a' to 6, 'q' to 7, 'Q' to 8, 't' to 9,
            'T' to 10, 'd' to 11, 'w' to 12, 'W' to 13, 'c' to 14,
            'z' to 15, 'x' to 16, 'v' to 17, 'g' to 18
        )

        // 중성 인덱스 매핑
        val jungSungIndex = mapOf(
            'k' to 0, 'o' to 1, 'i' to 2, 'O' to 3, 'j' to 4,
            'p' to 5, 'u' to 6, 'P' to 7, 'h' to 8, 'y' to 12,
            'n' to 13, 'b' to 17, 'm' to 18, 'l' to 20
        )

        // 복합 중성 인덱스 매핑
        val doubleJungSungIndex = mapOf(
            "hk" to 9, "ho" to 10, "hl" to 11, "nj" to 14,
            "np" to 15, "nl" to 16, "ml" to 19
        )

        // 종성 인덱스 매핑 (받침 없음 = 0)
        val jongSungIndex = mapOf(
            "r" to 1, "R" to 2, "rt" to 3, "s" to 4, "sw" to 5,
            "sg" to 6, "e" to 7, "f" to 8, "fr" to 9, "fa" to 10,
            "fq" to 11, "ft" to 12, "fx" to 13, "fv" to 14, "fg" to 15,
            "a" to 16, "q" to 17, "qt" to 18, "t" to 19, "T" to 20,
            "d" to 21, "w" to 22, "c" to 23, "z" to 24, "x" to 25,
            "v" to 26, "g" to 27
        )

        val result = StringBuilder()
        var i = 0

        while (i < eng.length) {
            // 초성 처리
            val initialChar = eng.getOrNull(i) ?: break
            val choSungValue = choSungIndex[initialChar]
            if (choSungValue == null) {
                // 초성이 아니면 그대로 추가
                result.append(initialChar)
                i++
                continue
            }
            i++

            // 중성 처리
            var jungSungValue: Int? = null
            if (i < eng.length) {
                // 2자 중성 확인
                if (i + 1 < eng.length) {
                    val twoChars = eng.substring(i, i + 2)
                    if (doubleJungSungIndex.containsKey(twoChars)) {
                        jungSungValue = doubleJungSungIndex[twoChars]
                        i += 2
                    }
                }

                // 1자 중성 확인
                if (jungSungValue == null) {
                    val jungSungChar = eng.getOrNull(i)
                    if (jungSungChar != null && jungSungIndex.containsKey(jungSungChar)) {
                        jungSungValue = jungSungIndex[jungSungChar]
                        i++
                    }
                }
            }

            // 중성이 없으면 초성 자모만 추가 (ㄱㄴㄷ 등)
            if (jungSungValue == null) {
                val jamoChar = when (choSungValue) {
                    0 -> 'ㄱ'; 1 -> 'ㄲ'; 2 -> 'ㄴ'; 3 -> 'ㄷ'; 4 -> 'ㄸ';
                    5 -> 'ㄹ'; 6 -> 'ㅁ'; 7 -> 'ㅂ'; 8 -> 'ㅃ'; 9 -> 'ㅅ';
                    10 -> 'ㅆ'; 11 -> 'ㅇ'; 12 -> 'ㅈ'; 13 -> 'ㅉ'; 14 -> 'ㅊ';
                    15 -> 'ㅋ'; 16 -> 'ㅌ'; 17 -> 'ㅍ'; 18 -> 'ㅎ';
                    else -> '?'
                }
                result.append(jamoChar)
                continue
            }

            // 종성 처리
            var jongSungValue = 0 // 기본값: 받침 없음
            if (i < eng.length) {
                // 2자 종성 확인
                if (i + 1 < eng.length) {
                    val twoChars = eng.substring(i, i + 2)
                    if (jongSungIndex.containsKey(twoChars)) {
                        // 다음 문자가 중성인지 확인
                        if (i + 2 < eng.length) {
                            val nextChar = eng[i + 2]
                            val nextTwoChars = if (i + 3 < eng.length) eng.substring(i + 2, i + 4) else ""

                            // 다음 글자가 중성이면 현재 글자는 종성이 아닌 다음 글자의 초성
                            if (jungSungIndex.containsKey(nextChar) || doubleJungSungIndex.containsKey(nextTwoChars)) {
                                // 종성 아님, 다음 글자로
                            } else {
                                jongSungValue = jongSungIndex[twoChars] ?: 0
                                i += 2
                            }
                        } else {
                            // 더 이상 글자가 없으면 종성으로 처리
                            jongSungValue = jongSungIndex[twoChars] ?: 0
                            i += 2
                        }
                    }
                }

                // 1자 종성 확인 (아직 종성이 결정되지 않은 경우)
                if (jongSungValue == 0) {
                    val jongSungChar = eng.getOrNull(i)
                    if (jongSungChar != null && jongSungIndex.containsKey(jongSungChar.toString())) {
                        // 다음 문자가 중성인지 확인
                        if (i + 1 < eng.length) {
                            val nextChar = eng[i + 1]
                            val nextTwoChars = if (i + 2 < eng.length) eng.substring(i + 1, i + 3) else ""

                            // 다음 글자가 중성이면 현재 글자는 종성이 아닌 다음 글자의 초성
                            if (jungSungIndex.containsKey(nextChar) || doubleJungSungIndex.containsKey(nextTwoChars)) {
                                // 종성 아님, 다음 글자로
                            } else {
                                jongSungValue = jongSungIndex[jongSungChar.toString()] ?: 0
                                i++
                            }
                        } else {
                            // 더 이상 글자가 없으면 종성으로 처리
                            jongSungValue = jongSungIndex[jongSungChar.toString()] ?: 0
                            i++
                        }
                    }
                }
            }

            // 유니코드 한글 조합: AC00(가) + (초성 * 21 + 중성) * 28 + 종성
            val unicodeValue = 0xAC00 + (choSungValue * 21 + jungSungValue) * 28 + jongSungValue
            result.append(unicodeValue.toChar())
        }

        return result.toString()
    }
}