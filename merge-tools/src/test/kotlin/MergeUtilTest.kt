import org.junit.jupiter.api.*
import org.mapins.mergetool.MergeDiffException
import org.mapins.mergetool.MergeFilter
import org.mapins.mergetool.MergeUtil
import org.mapins.mergetool.annotation.Diff
import org.mapins.mergetool.annotation.MergeBlocked
import org.mapins.mergetool.annotation.Mergeable
import org.mapins.mergetool.annotation.Property
import org.mapins.mergetool.filter.NotNullValuesFilter
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MergeUtilTest {

    private val mergeUtil = MergeUtil()
    
    @Throws(NoSuchMethodException::class)
    @Suppress("UNCHECKED_CAST")
    private fun extractMergeUtilClassCache(): MutableMap<KClass<*>, *> {
        val property = mergeUtil::class.declaredMemberProperties.first { it.name == "_classCache" }
        property.isAccessible = true
        return (property.getter.call(mergeUtil)) as MutableMap<KClass<*>, *>
    }
    

    @Throws(NoSuchMethodException::class)
    @Suppress("UNCHECKED_CAST")
    private fun getMergeUtilClassCacheFields(kcls: KClass<*>): Map<String, KProperty1<*, *>>{
        val obj = mergeUtilClassCache.get(kcls)!!
        val property = obj::class.declaredMemberProperties.first{ it.name == "fields" }
        property.isAccessible = true
        return (property.getter.call(obj)) as Map<String, KProperty1<*, *>>
    }

    private val mergeUtilClassCache by lazy { extractMergeUtilClassCache() }

    private fun clearMergeUtilCaches() {
        mergeUtilClassCache.clear()
    }

    @BeforeEach
    fun testStartup(){
        clearMergeUtilCaches()
    }

    @Test
    fun mergeUtilAtStartTest(){
        assertTrue { mergeUtilClassCache.isEmpty() }
    }

    @Nested
    inner class MergeableAnnotationTest {

        @Test
        fun caching_none_field_class_Test() {
            @Mergeable
            class Test()

            mergeUtil.cacheClass(Test::class)
            
            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).isEmpty() }
        }

        @Test
        fun define_class_filter_Test() {
            class TestFilter() : MergeFilter {
                override fun filter(fieldName: String, value: Any?): Boolean {
                    return true
                }
            }

            @Mergeable(filter = TestFilter::class)
            class Test ()

            mergeUtil.cacheClass(Test::class)
            
            assertTrue { mergeUtilClassCache.size == 1 }
        }

        @Test
        fun define_not_null_values_object_filter_Test() {

            @Mergeable(filter = NotNullValuesFilter::class)
            class Test ()

            mergeUtil.cacheClass(Test::class)
            
            assertTrue { mergeUtilClassCache.size == 1 }
        }

        @Test
        fun caching_mutable_public_property_Test() {
            @Mergeable
            class Test(
                var testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).size == 1 }
        }

        @Test
        fun caching_unmutable_public_property_Test() {
            @Mergeable
            class Test(
                val testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).size == 1 }
        }

        @Test
        fun caching_mutable_private_property_with_no_available_private_Test() {
            @Mergeable
            class Test(
                private var testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).isEmpty() }
        }

        @Test
        fun caching_mutable_private_property_with_available_private_Test() {
            @Mergeable(mergePrivate = true)
            class Test(
                private var testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).size == 1 }
        }

        @Test
        fun caching_unmutable_private_property_with_no_available_private_Test() {
            @Mergeable
            class Test(
                private val testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).isEmpty() }
        }

        @Test
        fun caching_unmutable_private_property_with_available_private_Test() {
            @Mergeable(mergePrivate = true)
            class Test(
                private val testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).size == 1 }
        }
    }

    @Nested
    inner class DiffAnnotationTest {

        @Test
        fun caching_none_field_class_Test() {
            @Diff(target = Void::class)
            class Test()

            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).isEmpty() }
        }

        @Test
        fun caching_mutable_public_property_Test() {
            @Diff(target = Void::class)
            class Test(
                var testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).size == 1 }
        }

        @Test
        fun caching_unmutable_public_property_Test() {
            @Diff(target = Void::class)
            class Test(
                val testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).size == 1 }
        }

        @Test
        fun caching_mutable_private_property_with_no_available_private_Test() {
            @Diff(target = Void::class)
            class Test(
                private var testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).isEmpty() }
        }

        @Test
        fun caching_mutable_private_property_with_available_private_Test() {
            @Diff(target = Void::class)
            class Test(
                private var testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).isEmpty() }
        }

        @Test
        fun caching_unmutable_private_property_with_no_available_private_Test() {
            @Diff(target = Void::class)
            class Test(
                private val testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).isEmpty() }
        }

        @Test
        fun caching_unmutable_private_property_with_available_private_Test() {
            @Diff(target = Test::class)
            class Test(
                private val testValue1: Int
            )
            mergeUtil.cacheClass(Test::class)

            assertTrue { mergeUtilClassCache.size == 1 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).isEmpty() }
        }

        @Test
        fun apply_caching_on_target_class_Test() {
            @Mergeable
            class Test(
                var testValue1: Int
            )

            @Diff(target = Test::class)
            class TestDiff(
                var testValue1: Int
            )

            val test = Test(0)
            val testDiff = TestDiff(1)

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
            assertTrue { mergeUtilClassCache.size == 2 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).size == 1}
            assertEquals(test.testValue1, testDiff.testValue1)
        }

        @Test
        fun apply_caching_on_not_target_class_Test() {
            @Mergeable
            class Test(
                var testValue1: Int
            )

            @Diff(target = Void::class)
            class TestDiff(
                var testValue1: Int
            )

            val test = Test(0)
            val testDiff = TestDiff(1)

            assertThrows<MergeDiffException> { mergeUtil.applyDiff(test, testDiff) }
            assertTrue { mergeUtilClassCache.size == 2 }
            assertTrue { getMergeUtilClassCacheFields(Test::class).size == 1 }
            assertNotEquals(test.testValue1, testDiff.testValue1)
        }
    }
    
    @Nested
    inner class MergeTest{

        @Test
        fun merge_empty_field_with_precaching(){
            @Mergeable
            class Test ()

            @Diff(target = Test::class)
            class TestDiff ()

            mergeUtil.cacheClass(Test::class)
            mergeUtil.cacheClass(TestDiff::class)

            val test = Test()
            val testDiff = TestDiff()

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
        }

        @Test
        fun merge_empty_field_with_no_caching(){
            @Mergeable
            class Test ()

            @Diff(target = Test::class)
            class TestDiff ()

            val test = Test()
            val testDiff = TestDiff()

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
        }

        @Test
        fun merge_empty_field_with_no_caching_and_no_annotations(){
            class Test ()

            class TestDiff ()

            val test = Test()
            val testDiff = TestDiff()

            assertThrows<MergeDiffException> { mergeUtil.applyDiff(test, testDiff) }
        }

        @Test
        fun merge_empty_field_with_no_caching_and_one_annotations(){
            @Mergeable
            class Test ()

            class TestDiff ()

            val test = Test()
            val testDiff = TestDiff()

            assertThrows<MergeDiffException> { mergeUtil.applyDiff(test, testDiff) }
        }

        @Test
        fun merge_empty_field_of_one_class_with_no_caching(){
            @Mergeable
            class Test ()

            val test = Test()
            val testDiff = Test()

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
        }

        @Test
        fun merge_one_mutable_public_field_with_no_precaching(){
            @Mergeable
            class Test (
                var test: Int = 0
            )

            @Diff(target = Test::class)
            class TestDiff (
                var test: Int = 1
            )


            val test = Test()
            val testDiff = TestDiff()

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
            assertEquals(test.test, testDiff.test)
            assertTrue { test.test == 1 }
            assertTrue { testDiff.test == 1 }
        }

        @Test
        fun merge_one_unmutable_public_field_with_no_and_without_unmutable_precaching(){
            @Mergeable
            class Test (
                val test: Int = 0
            )

            @Diff(target = Test::class)
            class TestDiff (
                var test: Int = 1
            )


            val test = Test()
            val testDiff = TestDiff()

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
            assertNotEquals(test.test, testDiff.test)
            assertTrue { test.test == 0 }
            assertTrue { testDiff.test == 1 }
        }

        @Test
        fun merge_one_unmutable_public_field_with_no_and_with_unmutable_precaching(){
            @Mergeable(mergeUnmutable = true)
            class Test (
                val test: Int = 0
            )

            @Diff(target = Test::class)
            class TestDiff (
                var test: Int = 1
            )


            val test = Test()
            val testDiff = TestDiff()

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
            assertEquals(test.test, testDiff.test)
            assertTrue { test.test == 1 }
            assertTrue { testDiff.test == 1 }
        }

        @Test
        fun merge_one_mutable_private_field_with_no_access_with_no_precaching(){
            @Mergeable(mergePrivate = false)
            class Test (
                private var test: Int = 0
            ){
                val testCheck get() = test
            }

            @Diff(target = Test::class)
            class TestDiff (
                var test: Int = 1
            )


            val test = Test()
            val testDiff = TestDiff()

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
            assertNotEquals(test.testCheck, testDiff.test)
            assertTrue { test.testCheck == 0 }
            assertTrue { testDiff.test == 1 }
        }

        @Test
        fun merge_one_mutable_private_field_with_access_with_no_precaching(){
            @Mergeable(mergePrivate = true)
            class Test (
                private var test: Int = 0
            ){
                val testCheck get() = test
            }

            @Diff(target = Test::class)
            class TestDiff (
                var test: Int = 1
            )


            val test = Test()
            val testDiff = TestDiff()

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
            assertEquals(test.testCheck, testDiff.test)
            assertTrue { test.testCheck == 1 }
            assertTrue { testDiff.test == 1 }
        }

        @Test
        fun merge_one_mutable_private_field_with_no_access_with_no_precaching_same_class(){
            @Mergeable(mergePrivate = false)
            class Test (
                private var test: Int
            ){
                val testCheck get() = test
            }

            val test = Test(0)
            val testDiff = Test(1)

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
            assertNotEquals(test.testCheck, testDiff.testCheck)
            assertTrue { test.testCheck == 0 }
            assertTrue { testDiff.testCheck == 1 }
        }

        @Test
        fun merge_one_mutable_private_field_with_access_with_no_precaching_same_class(){
            @Mergeable(mergePrivate = true)
            class Test (
                private var test: Int
            ){
                val testCheck get() = test
            }


            val test = Test(0)
            val testDiff = Test(1)

            assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
            assertEquals(test.testCheck, testDiff.testCheck)
            assertTrue { test.testCheck == 1 }
            assertTrue { testDiff.testCheck == 1 }
        }

        @Nested
        inner class PropertyAnnotationTest(){

            @Test
            fun merge_public_with_same_name_and_empty_annotation(){
                @Mergeable(mergePrivate = true)
                class Test (
                    var test: Int = 0
                )

                @Diff(target = Test::class)
                class TestDiff (
                    @Property
                    var test: Int = 1
                )


                val test = Test()
                val testDiff = TestDiff()

                assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
                assertEquals(test.test, testDiff.test)
                assertTrue { test.test == 1 }
                assertTrue { testDiff.test == 1 }
            }

            @Test
            fun merge_public_with_same_name_and_different_property_name_annotation(){
                @Mergeable(mergePrivate = true)
                class Test (
                    var test: Int = 0
                )

                @Diff(target = Test::class)
                class TestDiff (
                    @Property(applyOn = "different_name")
                    var test: Int = 1
                )


                val test = Test()
                val testDiff = TestDiff()

                assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
                assertNotEquals(test.test, testDiff.test)
                assertTrue { test.test == 0 }
                assertTrue { testDiff.test == 1 }
            }

            @Test
            fun merge_public_with_different_name_and_property_name_annotation(){
                @Mergeable(mergePrivate = true)
                class Test (
                    var test: Int = 0
                )

                @Diff(target = Test::class)
                class TestDiff (
                    @Property(applyOn = "test")
                    var differentName: Int = 1
                )


                val test = Test()
                val testDiff = TestDiff()

                assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
                assertEquals(test.test, testDiff.differentName)
                assertTrue { test.test == 1 }
                assertTrue { testDiff.differentName == 1 }
            }
        }

        @Nested
        inner class MergeBlockedCheck(){

            @Test
            fun merge_one_mutable_public_field_with_no_precaching(){
                @Mergeable
                class Test (
                    @MergeBlocked
                    var test: Int = 0
                )

                @Diff(target = Test::class)
                class TestDiff (
                    var test: Int = 1
                )


                val test = Test()
                val testDiff = TestDiff()

                assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
                assertNotEquals(test.test, testDiff.test)
                assertTrue { test.test == 0 }
                assertTrue { testDiff.test == 1 }
            }

            @Test
            fun merge_one_mutable_private_field_with_no_access_with_no_precaching(){
                @Mergeable(mergePrivate = false)
                class Test (
                    @MergeBlocked
                    private var test: Int = 0
                ){
                    val testCheck get() = test
                }

                @Diff(target = Test::class)
                class TestDiff (
                    var test: Int = 1
                )


                val test = Test()
                val testDiff = TestDiff()

                assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
                assertNotEquals(test.testCheck, testDiff.test)
                assertTrue { test.testCheck == 0 }
                assertTrue { testDiff.test == 1 }
            }

            @Test
            fun merge_one_mutable_private_field_with_access_with_no_precaching(){
                @Mergeable(mergePrivate = true)
                class Test (
                    @MergeBlocked
                    private var test: Int = 0
                ){
                    val testCheck get() = test
                }

                @Diff(target = Test::class)
                class TestDiff (
                    var test: Int = 1
                )


                val test = Test()
                val testDiff = TestDiff()

                assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
                assertNotEquals(test.testCheck, testDiff.test)
                assertTrue { test.testCheck == 0 }
                assertTrue { testDiff.test == 1 }
            }

            @Test
            fun merge_one_mutable_private_field_with_access_with_no_precaching_same_class(){
                @Mergeable(mergePrivate = true)
                class Test (
                    @MergeBlocked
                    private var test: Int = 0
                ){
                    val testCheck get() = test
                }


                val test = Test(0)
                val testDiff = Test(1)

                assertDoesNotThrow { mergeUtil.applyDiff(test, testDiff) }
                assertNotEquals(test.testCheck, testDiff.testCheck)
                assertTrue { test.testCheck == 0 }
                assertTrue { testDiff.testCheck == 1 }
            }
        }
    }
}