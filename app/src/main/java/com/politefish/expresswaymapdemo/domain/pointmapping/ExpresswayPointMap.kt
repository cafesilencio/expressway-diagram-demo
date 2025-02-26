package com.politefish.expresswaymapdemo.domain.pointmapping

import QuadkeyUtil
import com.mapbox.geojson.Point
import com.politefish.expresswaymapdemo.domain.locationsearch.ExpRouteSegment
import com.politefish.expresswaymapdemo.domain.locationsearch.KeyPointMetaData
import java.util.UUID

object ExpresswayPointMap {

    fun getSegmentsRelatedToPoint(point: Point): List<ExpRouteSegment> {
        val quadkeyForPoint = QuadkeyUtil.getQuadkey(point, 16)
        return getPointSegments2 { segment: ExpRouteSegment ->
            segment.quadkeys.contains(
                quadkeyForPoint
            )
        }
    }

    private fun getPointSegments2(predicate: (ExpRouteSegment) -> Boolean): List<ExpRouteSegment> {
        return listOf(
            ExpRouteSegment(
                listOf(
                    "1330021123321022",
                    "1330021123321020",
                    "1330021123321002",
                    "1330021123321000",
                    "1330021123303222",
                ),
                "mg{vbAgnesiG_{Ey|@svGijAuyGufAy|GaN",
                "fe3e525d-da99-4ee9-8c14-22a3e8ee87df",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(328.0f, 467.0f),
                        ExpBearing.UpRightDownLeft,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(344.0f, 450.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123303222",
                    "1330021123303220",
                    "1330021123303202",
                    "1330021123302313",
                    "1330021123302311",
                    "1330021123302133",
                    "1330021123302132",
                    "1330021123302130",
                    "1330021123302121",
                    "1330021123302103",
                    "1330021123302102",
                    "1330021123302100",
                ),
                "qs|wbAcnlsiG_nGjsBaaG|tDixFdpDc}FzsDuyF|rDapExfFa_DblHoxCbuH",
                "b697b203-4c0c-4b25-817e-244627378ead",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(344.0f, 450.0f),
                        ExpBearing.Vertical,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(344.0f, 427.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123302100",
                    "1330021123302011",
                    "1330021123302010",
                    "1330021123300232",
                    "1330021123300230",
                    "1330021123300212",
                    "1330021123300203",
                ),
                "kauybAw~wqiGmuC|rH_rEhfF{uGxyA_zGpvA",
                "0c2aef28-a6cd-4d66-b780-9e8b7bab26bb",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(344.0f, 427.0f),
                        ExpBearing.UpLeftDownRight,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(322.0f, 401.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123300203",
                    "1330021123300201",
                    "1330021123300023",
                    "1330021123300021",
                    "1330021123300003",
                ),
                "u|qzbAcqaqiGw_H`vAwgHpNkhHv@khHoB",
                "8ff50a47-b13c-445a-9c8d-55b026a42c3b",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(322.0f, 401.0f),
                        ExpBearing.Vertical,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(322.0f, 386.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123300003",
                    "1330021123300001",
                    "1330021123122223",
                    "1330021123122221",
                ),
                "}xv{bAgl~piGu{YkgA",
                "bcf6c557-895f-47c5-b8af-d16b3b42d5d7",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(322.0f, 386.0f),
                        ExpBearing.Vertical,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(322.0f, 379.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123122221",
                    "1330021123122230",
                    "1330021123122212",
                    "1330021123122213",
                    "1330021123122211",
                    "1330021123122300",
                    "1330021123122122",
                    "1330021123122123",
                    "1330021123122121",
                    "1330021123122130",
                    "1330021123122112",
                    "1330021123122113",
                ),
                "suq|bAst`qiGipEwwFsaEs|FsaEs|FabEc|FemDegHecDymHq_DchH",
                "618e1bae-f3cc-43e0-8891-3c58b634098e",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(322.0f, 379.0f),
                        ExpBearing.UpRightDownLeft,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(341.0f, 360.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123122113",
                    "1330021123123002",
                    "1330021123123000",
                    "1330021123123001",
                    "1330021123121223",
                    "1330021123121232",
                ),
                "ebz}bA{e|riGkcDmlHu|CccHq_DikH",
                "72a3627e-d45f-4101-b3a6-1a5b75eca29e",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(341.0f, 360.0f),
                        ExpBearing.UpRightDownLeft,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(358.0f, 343.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123121232",
                ),
                "ydi~bAwcxsiG}wBykI",
                "5837925a-7d93-4849-8189-5bccc98dd049",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(358.0f, 343.0f),
                        ExpBearing.UpRightDownLeft,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(363.0f, 338.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123121232",
                    "1330021123121233",
                    "1330021123121231",
                    "1330021123121320",
                ),
                "w}l~bAqpbtiG_lBorIucByfI",
                "4adbab11-c155-407b-9d48-b9b576787c9c",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(363.0f, 338.0f),
                        ExpBearing.Horizontal,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(389.0f, 338.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123121320",
                    "1330021123121321",
                    "1330021123121330",
                    "1330021123121331",
                    "1330021123130220",
                    "1330021123130221",
                    "1330021123130230",
                    "1330021123130231",
                    "1330021123130320",
                ),
                "mos~bA{kwtiG}d@u{Ic`@gbJ{b@agJwHmiJxUacJxSezInW_fJ",
                "329cd22b-18ae-4c6e-b533-8d5984673f16",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(389.0f, 338.0f),
                        ExpBearing.Horizontal,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(421.0f, 338.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123130320",
                    "1330021123130321",
                ),
                "_`u~bAqdewiGfbB{dI",
                "73ed2af3-e69e-4d96-9655-c12d6c01fcc7",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(421.0f, 338.0f),
                        ExpBearing.Horizontal,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(426.0f, 338.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123130321",
                    "1330021123130323",
                    "1330021123130332",
                    "1330021123130333",
                    "1330021123132111",
                    "1330021123133000",
                ),
                "w|q~bAmjowiGrsBi|HtsBg|HtrBy|HtcBceI",
                "13bdc107-db73-4fa6-ac27-b5712281c51c",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(426.0f, 338.0f),
                        ExpBearing.UpLeftDownRight,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(440.0f, 351.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123133000",
                    "1330021123133001",
                    "1330021123133010",
                    "1330021123131232",
                    "1330021123131233",
                    "1330021123131231",
                ),
                "a{c~bA}hwxiGup@o~Io{DecGokE}sF",
                "4c8abde5-832d-4161-afda-aa93c6c3fa4f",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(440.0f, 351.0f),
                        ExpBearing.Horizontal,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(450.0f, 351.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf(
                    "1330021123131231",
                    "1330021123131320",
                    "1330021123131302",
                    "1330021123131303",
                    "1330021123131301",
                    "1330021123131310",
                    "1330021123131132",
                    "1330021123131133",
                    "1330021123131131",
                    "1330021123131113",
                    "1330021132020002",
                    "1330021132020000",
                    "1330021132020001",
                    "1330021132002223",
                    "1330021132002232",
                    "1330021132002230",
                    "1330021132002231",
                    "1330021132002213",
                    "1330021132002302",
                ),
                "wuq~bAqaryiGmkE}sF{sE_~FaqEqzFkrEq|FwrEovFe}EwrEyaF}sEauEulFqoEkpFkqEsjFkfEkkGujDiuGiiD{tG",
                "0a5f4b2d-5788-4988-8446-74d6408fa126",
                3.0,
                listOf(
                    KeyPointMetaData(
                        0,
                        Pair(450.0f, 351.0f),
                        ExpBearing.UpRightDownLeft,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(492.0f, 310.0f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf("1330021123122112", "1330021123122101", "1330021123122102"),
                "srw}bA}`wriGstEvpFq{AtmI`_BhfI",
                UUID.randomUUID().toString(),
                3.0,
                listOf<KeyPointMetaData>(
                    KeyPointMetaData(
                        0,
                        Pair(341f, 360f),
                        ExpBearing.UpLeftDownRight,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(324f, 342f),
                        ExpBearing.Vertical,
                        ExpBearing.NoOp,
                    ),
                )
            ),
            ExpRouteSegment(
                listOf("1330021123121223", "1330021123121222", "1330021123120331", "1330021123120313", "1330021123120310"),
                "ojg~bAysmsiG{nFxzDuxF~dEq_FfwEg~D~aE",
                UUID.randomUUID().toString(),
                3.0,
                listOf<KeyPointMetaData>(
                    KeyPointMetaData(
                        0,
                        Pair(356f, 339f),
                        ExpBearing.UpLeftDownRight,
                        ExpBearing.NoOp,
                    ),
                    KeyPointMetaData(
                        1,
                        Pair(346f, 330f),
                        ExpBearing.NoOp,
                        ExpBearing.NoOp,
                    ),
                )
            ),
        ).filter(predicate)
    }
}
