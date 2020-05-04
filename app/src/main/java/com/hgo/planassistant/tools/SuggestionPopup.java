package com.hgo.planassistant.tools;

import android.content.Context;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;

import com.hgo.planassistant.R;

import java.util.List;
import java.util.Random;

public class SuggestionPopup {
    private String[] PhysicalEnergyTitle = new String[3];
    private String[] PhysicalEnergyDescription = new String[3];
    private String[] EmotionEnergyTitle = new String[3];
    private String[] EmotionEnergyDescription = new String[3];
    private String[] ThinkingEnergyTitle = new String[1];
    private String[] ThinkingEnergyDescription = new String[1];
    private String[] DeterminationEnergyTitle = new String[1];
    private String[] DeterminationEnergyDescription = new String[1];

    private String urgent_important = new String();
    private String noturgent_important = new String();
    private String urgent_unimportant = new String();
    private String noturgent_unimportant = new String();

    public SuggestionPopup(){
        PhysicalEnergyTitle[0] = "调整饮食方式。";
        PhysicalEnergyTitle[1] = "调整生理周期与睡眠。";
        PhysicalEnergyTitle[2] = "调整每天的工作/生活节奏。";

        PhysicalEnergyDescription[0] =
                "1. 选择升糖指数低的食物（如全麦食物、蛋白质和低糖水果——草莓、梨子和苹果等）\n" +
                "2. 一天内五至六餐低热量而高营养的食物能够供应稳定的精力。\n" +
                "3. 让自己更加熟悉进食适当的程度——不感到饥饿也不感到撑胀。\n" +
                "4. 多喝水，当我们感到口渴的时候，身体或许已经缺水很久了。 \n" +
                "(一家研究机构称，每天至少饮用1.8公斤水对维持体能有许多好处。) \n" +
                "（小提示：摄入咖啡和咖啡因饮料可能对心脏有害。与高糖食物一样，咖啡因饮料如咖啡、茶和健怡可乐能瞬间提升精力，但由于咖啡因利尿，长期饮用依旧会导致缺水和疲倦。）";
        PhysicalEnergyDescription[1] =
                "1. 即便少量的睡眠缺失（《精力管理》一书中称其为精力再生不足）也会深刻影响力量、心血管能力、情绪和整体精力水平。大约有50项研究表明, 思维能力——即反应时间、专注力、记忆力、逻辑分析及辩证能力——会随着睡眠不足而衰退。建议您保证每天7～8小时的规律睡眠。\n" +
                "2. 在一项大型的研究中,心理学家丹·克里普克与同事追踪了100万人在六年间的睡眠模式,每晚睡7~8小时的人死亡率最低,睡眠不足4小时的人死亡率较前者高出2.5倍,而睡眠超过10小时的人死亡率相比高出1.5倍。简而言之,精力再生不足或再生过量都会增加死亡的风险。\n" +
                "3. 如果您无法保证7～8小时完整的睡眠，我们建议您尽量每4小时小憩20～30分钟。睡眠研究员、心理学家克劳迪奥·斯坦皮证明了这种方法可以在不能长时间睡眠的情况下仍保持超过24小时的惊人高效和敏锐。唯一需要注意的地方就是小憩需要定时，以免受试者陷入更深的睡眠中。如果小憩超出30或40分钟，许多受试者会感到眩晕无力,甚至比不睡更加疲倦。";
        PhysicalEnergyDescription[2] = "我们在晚间经历多次睡眠循环,白天里精力的潜能也在不断变化。精力的波动与次昼夜节律息息相关，生理信号以90~120分钟为周期。为了保持全情投入的状态，建议您每工作90~120分钟就休息片刻。";
        EmotionEnergyTitle[0] = "获得正面情感。";
        EmotionEnergyTitle[1] = "学会从跌倒处站起。";
        EmotionEnergyTitle[2] = "让人际关系促使精力再生。";
        EmotionEnergyDescription[0] = "所有能带来享受、满足和安全感的活动都能够激发正面情感。由于人们兴趣各异,这些活动可能是唱歌,园艺,跳舞,亲热,练瑜伽,读书,体育运动,参观博物馆,听音乐会,或者仅仅是在忙碌地社交之后静坐自省。因此我们建议您做一些令自己享受、满足或是能给自己带来安全感的活动来激发您的正向情感。";
        EmotionEnergyDescription[1] = "有时我们会不由自主地经历情感风暴,接受人生汹涌而来的挑战。让自己被风暴淹没还是在风暴中成长,取决于我们的情感管理方式。";
        EmotionEnergyDescription[2] = "盖洛普公司发现，保持优秀表现的诀窍之一是在工作环境中至少交一位好朋友。一段稳固的关系包括付出与回报、倾诉与倾听、珍视他人和被人同等珍视。因此我们建议您从人际关系中获取力量，在友谊的滋养下改善您的精力！";

        ThinkingEnergyTitle[0] = "在放松中思考。";
        ThinkingEnergyDescription[0] = "保持专注与乐观的秘诀在于间歇地变换思维频道,达到精力休息和再生的效果。思维恢复的关键是让正常工作的大脑间歇地休息。因此我们建议您间歇性的休息以保持专注与乐观。";

        DeterminationEnergyTitle[0] = "知晓生命的意义方能忍耐一切。";
        DeterminationEnergyDescription[0] = "生而没有意义的人是痛苦的,没有目标、没有目的、无需继续忍受。将个人利益置后起初会让人倍感不安,但加里却认为这样做回报颇丰——提升个人价值、丰富人生意义。深层的价值取向所带来的生活方式不仅是生活的主心骨,还能帮助我们更好地应对各种挑战。意志精力为所有层面的行为提供动力,带来激情、恒心和投入。意志精力源于价值取向和超出个人利益的目标。因此我们建议您将个人利益置后，提升个人价值，丰富人生意义。";

        urgent_important = "<p>该类日程包括：危机、迫切问题以及在限定时间内必须完成等日程。精力分配偏重这一类日程的结果常常是：</p>" +
                "<p> &#8226; 压力大 </p>" +
                "<p> &#8226; 筋疲力尽 </p>" +
                "<p> &#8226; 被危机牵着鼻子走 </p>" +
                "<p> &#8226; 忙于收拾残局 </p>";

        noturgent_important = "<p>该类日程包括：预防性措施、建立关系、明确新的发展机会以及制定计划和休闲等日程。精力分配偏重这一类日程的结果常常是：</p>" +
                "<p> &#8226; 愿景、远见 </p>" +
                "<p> &#8226; 平衡 </p>" +
                "<p> &#8226; 自律 </p>" +
                "<p> &#8226; 自制 </p>" +
                "<p> &#8226; 很少发生危机 </p>";

        urgent_unimportant = "<p>该类日程包括：接待访客、某些电话、某些邮件、某些报告、某些会议、迫切需要解决的事务以及公共活动等日程。精力分配偏重这一类日程的结果常常是：</p>" +
                "<p> &#8226; 急功近利 </p>" +
                "<p> &#8226; 被危机牵着鼻子走 </p>" +
                "<p> &#8226; 被视为巧言令色 </p>" +
                "<p> &#8226; 轻视目标和计划 </p>" +
                "<p> &#8226; 认为自己是受害者，缺乏自制力 </p>" +
                "<p> &#8226; 人际关系肤浅，甚至破裂 </p>";

        noturgent_unimportant = "<p>该类日程包括：繁琐忙碌的工作、某些电话、某些邮件、消磨时间的活动以及令人愉快的活动等日程。精力分配偏重这一类日程的结果常常是：</p>" +
                "<p> &#8226; 完全不负责任 </p>" +
                "<p> &#8226; 被炒鱿鱼 </p>" +
                "<p> &#8226; 基本生活都需要依赖他人或社会机构 </p>";
    }

    public String[] getPhysicalEnergyTitle() {
        return PhysicalEnergyTitle;
    }

    public String[] getDeterminationEnergyTitle() {
        return DeterminationEnergyTitle;
    }

    public String[] getDeterminationEnergyDescription() {
        return DeterminationEnergyDescription;
    }

    public String[] getEmotionEnergyDescription() {
        return EmotionEnergyDescription;
    }

    public String[] getEmotionEnergyTitle() {
        return EmotionEnergyTitle;
    }

    public String[] getPhysicalEnergyDescription() {
        return PhysicalEnergyDescription;
    }

    public String[] getThinkingEnergyDescription() {
        return ThinkingEnergyDescription;
    }

    public String[] getThinkingEnergyTitle() {
        return ThinkingEnergyTitle;
    }

    public String getPhysicalTitle(int i){
        return PhysicalEnergyTitle[i];
    }
    public String getPhysicalDescription(int i){
        return PhysicalEnergyDescription[i];
    }
    public String getEmotionalTitle(int i){
        return EmotionEnergyTitle[i];
    }
    public String getEmotionalDescription(int i){
        return EmotionEnergyDescription[i];
    }
    public String getThinkingTitle(int i){
        return ThinkingEnergyTitle[i];
    }
    public String getThinkingDescription(int i){
        return ThinkingEnergyDescription[i];
    }
    public String getDeterminationTitle(int i){
        return DeterminationEnergyTitle[i];
    }
    public String getDeterminationDescription(int i){
        return DeterminationEnergyDescription[i];
    }
    public void RandomPhysicalPopup(Context context){
        Random random = new Random();//默认构造方法
        int index = random.nextInt(3); //获取[0, 3)之间的int整数
        new AlertDialog.Builder(context)
                .setTitle("精力优化建议：" + getPhysicalTitle(index))
                .setMessage(getPhysicalDescription(index))
                .setPositiveButton(context.getString(R.string.dialog_ok), null)
                .show();
    }
    public void RandomEmotionalPopup(Context context){
        Random random = new Random();//默认构造方法
        int index = random.nextInt(3); //获取[0, 3)之间的int整数
        new AlertDialog.Builder(context)
                .setTitle("精力优化建议：" + getEmotionalTitle(index))
                .setMessage(getEmotionalDescription(index))
                .setPositiveButton(context.getString(R.string.dialog_ok), null)
                .show();
    }
    public void RandomThinkingPopup(Context context){
        Random random = new Random();//默认构造方法
        int index = random.nextInt(1); //获取[0, 3)之间的int整数
        new AlertDialog.Builder(context)
                .setTitle("精力优化建议：" + getThinkingTitle(index))
                .setMessage(getThinkingDescription(index))
                .setPositiveButton(context.getString(R.string.dialog_ok), null)
                .show();
    }
    public void RandomDeterminationPopup(Context context){
        Random random = new Random();//默认构造方法
        int index = random.nextInt(1); //获取[0, 3)之间的int整数
        new AlertDialog.Builder(context)
                .setTitle("精力优化建议：" + getDeterminationTitle(index))
                .setMessage(getDeterminationDescription(index))
                .setPositiveButton(context.getString(R.string.dialog_ok), null)
                .show();
    }

    public void GetUrgentImportantPopup(Context context){
        new AlertDialog.Builder(context)
                .setTitle("紧迫又重要类日程")
                .setMessage( Html.fromHtml(urgent_important))
                .setPositiveButton(context.getString(R.string.dialog_ok), null)
                .show();
    }
    public void GetNotUrgentImportantPopup(Context context){
        new AlertDialog.Builder(context)
                .setTitle("不紧迫重要类日程")
                .setMessage( Html.fromHtml(noturgent_important))
                .setPositiveButton(context.getString(R.string.dialog_ok), null)
                .show();
    }
    public void GetUrgentUnimportantPopup(Context context){
        new AlertDialog.Builder(context)
                .setTitle("紧迫不重要类日程")
                .setMessage( Html.fromHtml(urgent_unimportant))
                .setPositiveButton(context.getString(R.string.dialog_ok), null)
                .show();
    }
    public void GetNotUrgentUnimportantPopup(Context context){
        new AlertDialog.Builder(context)
                .setTitle("不紧迫又不重要类日程")
                .setMessage( Html.fromHtml(noturgent_unimportant))
                .setPositiveButton(context.getString(R.string.dialog_ok), null)
                .show();
    }
}
