package com.hjnerp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2017/2/20.
 */

public class HJAboutDate {
    public static List<HJAboutBean> list = new ArrayList<>();

    /**
     * 公司简介
     *
     * @return
     */
    public static List<HJAboutBean> getGSJJ() {
        list.clear();
        HJAboutBean hjAboutBean = new HJAboutBean();
        hjAboutBean.setTitle_one("公司简介");
        hjAboutBean.setContent_hj("      北京和佳软件技术有限公司（简称和佳）由陈佳先生创办于1998年，是国内最专业的管理软件、云服务、大数据解决方案和服务提供商。 \n" +
                "      和佳软件自成立以来一直以用户需求为导向，以管理软件开发为核心，始终坚持自主产权软件研发，面对行业特色和用户个性需求为用户提供信息化整体解决方案及咨询、实施、维护服务。\n" +
                "      和佳团队拥有管理咨询、信息化规划、软件开发及应用、系统集成、云服务、大数据应用等各方面的专家，其研发能力和技术水平始终于国内领先地位。\n" +
                "      和佳软件的客户涉及多个行业，迄今为止已为2万多家中大小型企业进行了ERP等管理软件的实施及服务，尤其在装备、快速消费品、制药、出版等行业积累了深厚的研发及实施经验。\n" +
                "      随着互联网+时代的到来，和佳积极涉足云服务和大数据领域，为客户提供迁移、云应用服务等，并基于18年来的企业运营数据积累和行业专业经验积累构建了大数据生态体系。\n" +
                "      和佳软件低调、务实、专业、专注，在业界树立了良好的品牌形象。和佳软件持“一日为客户终身为客户”的服务宗旨，在客户群中拥有着非常好的口碑。\n");
        list.add(hjAboutBean);
        return list;
    }

    /**
     * 发展历程
     *
     * @return
     */
    public static List<HJAboutBean> getFZLC() {
        list.clear();
        HJAboutBean hjAboutBean1 = new HJAboutBean();
        hjAboutBean1.setTitle_one("和佳1.0  做中国最好的ERP");
        hjAboutBean1.setTitle_two("中国最早、最专业的ERP，拥有自主品牌和知识产权");
        hjAboutBean1.setContent_hj("      1998年3月24日和佳软件成立，并于同年推出和佳ERPV1.0以来，和佳软件多次参加国家经贸委、信息产业部和科技部863/CIMS项目组和软件行业协会组织的软件评比、评测、并获得荣誉。\n" +
                "      1999年3月，世界银行选中和佳财务软件，用于世界银行在华1200多个贷款项目单位的财务管理。\n" +
                "      2000年6月国家683/CIMS重大目标产品项目公开评测，和佳ERP被评为全国ERP产品第二名，获得“优秀软件”奖\n" +
                "      2006年8月，CCID对国内EFP厂商的客户满意度调中，和佳获得１４个项目中的７个第一名，并荣获满意度综合结果的第一名。\n");
        list.add(hjAboutBean1);

        HJAboutBean hjAboutBean2 = new HJAboutBean();
        hjAboutBean2.setTitle_one("和佳2.0  企业信息化综合服务商 突破了传ERP的范畴");
        hjAboutBean2.setTitle_two(
                "产品：" +
                "\nERP、CRM、DRP、集团管控、成本推演、全面费用管理、供应商门户 " +
                "\n服务：" +
                "\n管理咨询、IT规划、应用集成、系统集成、产品外包、项目实施与维护");
        hjAboutBean2.setContent_hj("   2004年1月，和佳实施的东阿阿胶ERP项目被国家经贸委评为中国企业管理信息化十大经典案例。\n" +
                "      2004年6月，由和佳负责实施的常林股份ERP项目被科技部评为国家制造业信息化重大工程全国十三家示范工程之一。\n" +
                "      2005年3月，由和佳成功实施的紫竹药业EPR、CRM、DRP、OA等经综合信息化项目，被科技部评为国家制造业信息化重大工程全国示范工程及“2004年中国制造信息化典型成功案例”。\n" +
                "      2008年1月，在2008百家优秀管理软件与企业交流会上，和佳NERP被评选为“2007年度快速消费品最佳ERP解决方案”\n");
        list.add(hjAboutBean2);

        HJAboutBean hjAboutBean3 = new HJAboutBean();
        hjAboutBean3.setTitle_one("和佳3.0  互联网应用综合服务商");
        hjAboutBean3.setTitle_two("和佳开发了\n独立电商平台、O2O业务应用、移动APP应用、微信应用等互联网产品");
        hjAboutBean3.setContent_hj("      2012年和佳依托云计算技术，推出了CERP、企业微博、执行力软件等新产品。\n" +
                "      2013年和佳成功多洗、多彩饰家等互联网企业的O2O业务系统。\n" +
                "      2015年和佳成功为山推，中丽制机等老客户成功实施独立电商平台，实现了原有的ERP系统的升级。\n" +
                "      2012年到2015年为燕京体系整体实施为移动ERP系统。\n" +
                "      2016年和佳成功实施了Artbean的O2O业务系统。\n");
        list.add(hjAboutBean3);


        HJAboutBean hjAboutBean4 = new HJAboutBean();
        hjAboutBean4.setTitle_one("和佳4.0  云服务及大数据解决方案提供商");
        hjAboutBean4.setTitle_two("和佳产品线扩展到：\n云基础设施、云平台、大数据、云应用");
        hjAboutBean4.setContent_hj("      2015年4月，和佳牵手阿里开发云端企业信息化系统，从迁云到云ERP运维提供全面服务。\n" +
                "      2015年5月，和佳牵手钉钉打造云端办公自动化业务。\n" +
                "      2015年9月，和佳牵手赛伯乐成功转型为大数据解决方案提供商\n");
        list.add(hjAboutBean4);
        return list;
    }

    /**
     * 公司荣誉
     *
     * @return
     */
    public static List<HJAboutBean> getGSRY() {
        list.clear();
        HJAboutBean hjAboutBean = new HJAboutBean();
        hjAboutBean.setTitle_one("自1998年8月推出《和佳ERP》V1.0以来，和佳软件在国家经贸委、信息产业部、科技部863/CIMS项目组，软件行业协会等多家权威机构的历年软件评比、评测中，获得很多荣誉。");
        hjAboutBean.setContent_hj("      1999年3月，世界银行选中《和佳财务》，用于世行在华1200多个贷款项目单位的财务管理\n \n" +
                "      1999年4月，《和佳ERP》被列为科技部“科技型中小企业创新基金”无偿援助项目，并于2001年5月第一个通过验收\n \n" +
                "      2000年4月，国家经贸委专门以“企改司函[2000]005号文”的形式在全国国有企业系统内推荐使用《和佳比质比价采购管理软件》\n \n" +
                "      2000年6月，国家863/CIMS重大目标产品项目公开评测，《和佳ERP》被评为全国ERP产品第二名，获“优秀软件”奖\n \n" +
                "      2001年4月，《和佳ERP》、《和佳比质比价采购管理软件》被中国软件行业协会评为“2000年度优秀软件”\n \n" +
                "      2001年6月，《和佳ERP》、《和佳比质比价采购管理软件》获得2001中国国际软件博览会金奖\n \n" +
                "      2002年4月，《和佳ERP》、《和佳CRM》、《和佳BOA》获得2002中国国际软件博览会金奖，《和佳DRP》获得创新奖\n \n" +
                "      2002年8月， CCID对国内ERP厂商的客户满意度调查中，和佳获得14个项目中的7个第一名，并荣获满意度综合结果的第一名\n \n" +
                "      2002年9月，《和佳ERP》再次摘取2002年度CCID\"制造业优秀产品奖\"\n \n" +
                "      2002年10月，国家863计划ERP课题评审结束：和佳代表国内中标的八家ERP厂商与科技部签定ERP课题总承包合同\n \n" +
                "      2002年10月，再与科技部签署课题任务书，承担国家863“典型数据库应用软件系统研发”课题\n \n" +
                "      2003年4月，国家863计划NERP（代表中国未来五年发展方向的“新一代ERP产品”）课题全国招标，和佳获得方案及ERP产品综合评测第一名\n \n" +
                "      2003年10月，和佳参与指定ERP国家标准，规范行业竞争准则，并最终完成了标准的报批工作\n" +
                "      2003年12月，中国IT用户年会两项大奖，机械行业ERP解决方案用户满意度奖，ERP解决方案设计创新用户满意奖\n \n" +
                "      2004年1月，和佳实施的东阿阿胶ERP项目被评为中国企业管理信息化十大经典案例\n \n" +
                "      2004年6月，和佳实施的常林股份ERP被评为国家制造业信息化重大工程全国十三大试点工程\n \n" +
                "      2004年7月，《和佳ERP》、《和佳CRM》、《和佳BOA》获得2004中国国际软件博览会金奖\n \n" +
                "      2005年1月，和佳荣获国家生产力促进中心颁发的“全国生产力促进奖”\n  \n" +
                "      2005年3月，《和佳ERP》被评为“2004年中国制造业信息化优秀推荐产品”\n  \n" +
                "      2005年3月，和佳实施的紫竹药业及被评为国家制造业信息化重大工程全国试点工程之一及“2004年中国制造业信息化典型成功案例”\n  \n" +
                "      2005年3月，和佳公司承担的并获得政府资金支持的国家863计划项目《基于国产DBMS的和佳ERP系统研发》顺利通过了科技部专家组的验收\n \n" +
                "      2005年4月，和佳公司承担的中科院知识创新工程重大项目《基于核心平台的企业信息化应用示范项目》顺利通过了中科院专家组的验收\n  \n" +
                "      2006年1月，在“中国制造业信息化趋势新年论坛暨MIE风云榜颁奖盛典”上，和佳荣膺“中国制造业信息化十大优秀供应商”及“编辑推荐精英单位”奖\n  \n" +
                "      2006年1月，在第三届中国信息产业年度优秀企业评选与产品推荐颁奖大会上，和佳软件荣获“2005年度ERP产品覆盖率领先企业”、“2005年度中国信息产业金牌服务企业”、《和佳DSS》荣获“2005年度中国信息产业创新产品”三项大奖\n  \n" +
                "      2006年2月，和佳公司承担的并获得政府资金支持的国家863计划项目《面向制造业的新一代企业资源计划系统研究与开发》顺利通过了科技部专家组的验收\n  \n" +
                "      2006年5月，由AMT研究院组织、历时四个月的首届中国管理软件与实施商2005年度评选落下帷幕，和佳公司荣获的是“企业资源计划-Enterprise Resources Planning，（简称ERP）软件领域国内十强软件商”奖\n  \n" +
                "      2006年9月，和佳公司承担的并获得政府资金支持的国家863计划项目《与和佳新一代ERP结合的供应链管理SCM与客户关系管理CRM系统的开发与应用》顺利通过了科技部专家组的验收。\n  \n" +
                "      2007年1月，和佳荣评“2006年制药行业最佳ERP供应商”及“中国管理软件十大自主品牌”\n \n" +
                "      2007年1月，和佳荣膺“中国制造业信息化2006年度优秀供应商”奖；陈佳总裁获评“中国信息产业十大风云人物”\n \n" +
                "      2008年1月，和佳在“2008百家优秀管理软件与企业交流会”上荣膺三项大奖：“2007年度十大国内自主品牌”、“2007年度十大品牌厂商”、“2007年度快速消费品最佳ERP解决方案”\n  \n" +
                "      2008年1月，和佳荣获“中国制造业信息化工程管理创新“奖，陈佳总裁荣获“中国软件行业十大年度风云人物”奖项\n \n" +
                "      2008年12月，和佳获评“2008年度十大管理软件自主品牌”，和佳CEO兼总裁陈佳先生获评“中国信息化最具影响力35人”\n  \n" +
                "      2009年1月，和佳软件获得“2008中国制造业信息化工程管理创新支撑奖”，北京、上海、广东、浙江、江西、河南、安徽、山西、山东等十余个省、市经贸委或科技厅将《和佳ERP》列为当地首推企业信息化产品解决方案。在国内高等学府，目前已有包括北京大学光华管理学院、清华大学软件学院、吉林大学管理学院在内的10所著名高校采用了和佳ERP作为教学示范软件。\n  \n" +
                "      2010年1月，和佳软件荣膺2009MIE创新之星风云榜管理创新支撑奖。\n  \n" +
                "      2010年5月，和佳ERP9.0再获用户满意奖。\n  \n" +
                "      2012年3月，和佳软件获评“2011制造业信息化杰出供应商”。\n  \n");
        list.add(hjAboutBean);
        return list;
    }
}
