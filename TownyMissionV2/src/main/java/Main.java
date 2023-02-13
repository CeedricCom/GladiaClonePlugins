import me.deltaorion.townymissionsv2.configuration.MessageManager;
import me.deltaorion.townymissionsv2.util.RandomHelper;
import org.bukkit.Sound;

import java.nio.file.FileSystems;
import java.text.MessageFormat;
import java.util.*;

public class Main {

    public static Main main = new Main();

    public static void main(String[] args) {
        main.run();
    }

    public void run() {
        long before = System.currentTimeMillis();
        MessageFormat format = new MessageFormat("");
        long after = System.currentTimeMillis();
        System.out.println(after - before);
    }

    public int search(int[] nums, int target) {
        int pivot = findPivot(nums);
        return pivotBinary(nums,pivot,target);
    }

    private int pivotBinary(int[] nums, int pivot, int target) {
        int left = 0;
        int right = nums.length -1;
        while (left <= right) {
            int middle = (left + right) / 2;
            int translate = translate(nums,pivot,middle);
            if(nums[translate] == target) {
                return translate;
            } else if(nums[translate] > target) {
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }

        return -1;
    }

    private int translate(int[] nums, int pivot, int position) {
        return (pivot + position) % nums.length;
    }

    private int findPivot(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int middle = (left + right)/2;
            if(middle == nums.length - 1) {
                //case 1, we have reached the edge of the array, in this case we are at the "pivot"
                return middle;
            } else if(nums[middle] > nums[middle+1]) {
                //case 2, the middle > than the next one, which means
                //we found the pivot because the array is supposed to be
                //in sorted order
                return middle + 1;
            } else if(nums[middle] < nums[nums.length-1]) {
                //if the current is less than the end, then the pivot lies
                //to left as this part is correctly in ascending order
                right = middle - 1;
            } else {
                //otherwise the pivot is to the right
                left = middle + 1;
            }
        }
        //in this case we navigated to the start with no match which means
        //there is no pivot

        return 0;
    }

}
