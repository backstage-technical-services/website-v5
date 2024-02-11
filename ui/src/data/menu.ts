import {
  mdiAccountMultiple,
  mdiCalendar,
  mdiCalendarPlus,
  mdiCalendarCheck,
  mdiFormatQuoteOpen,
  mdiToolbox,
  mdiTableMultiple,
  mdiStar,
  mdiStarPlus,
  mdiTableStar,
  mdiUpload,
  mdiFileSearch,
  mdiBandage,
  mdiHardHat,
  mdiTrophy,
  mdiVote,
} from '@quasar/extras/mdi-v7'

const v4 = () => Math.floor(Math.random() * 1000).toString(10)

export type MainMenuItem = {
  id?: string
  text?: string
  icon?: string
  link?: string
  children?: MainMenuItem[]
}

export type MainMenu = MainMenuItem[]

const menu: MainMenu = [
  {
    id: v4(),
    text: 'Events',
    children: [
      { id: v4(), text: 'Events diary', link: '/events-diary', icon: mdiCalendar },
      { id: v4(), text: 'Create event', link: '/events/add', icon: mdiCalendarPlus },
      { id: v4(), text: 'Submit event report', link: '/events/submit-report', icon: mdiCalendarCheck },
    ],
  },
  {
    id: v4(),
    text: 'Members',
    children: [
      { id: v4(), text: 'The membership', link: '/membership', icon: mdiAccountMultiple },
      { id: v4(), text: 'Quotes board', link: '/quotesboard', icon: mdiFormatQuoteOpen },
      { id: v4(), text: 'Awards', link: '/awards', icon: mdiTrophy },
      { id: v4(), text: 'Elections', link: '/elections', icon: mdiVote },
    ],
  },
  {
    id: v4(),
    text: 'Equipment',
    children: [
      { id: v4(), text: 'Asset database', link: '/asset-database', icon: mdiTableMultiple },
      { id: v4(), text: 'Repairs database', link: '/repairs-database', icon: mdiToolbox },
    ],
  },
  {
    id: v4(),
    text: 'Training',
    children: [
      { id: v4(), text: 'Skills', link: '/skills', icon: mdiStar },
      { id: v4(), text: 'Skill applications', link: '/skill-applications', icon: mdiStarPlus },
      { id: v4(), text: 'Skill categories', link: '/skill-categories', icon: mdiTableStar },
    ],
  },
  {
    id: v4(),
    text: 'Safety',
    children: [
      { id: v4(), text: 'Report incident', link: '/report/incident', icon: mdiBandage },
      { id: v4(), text: 'Report near miss', link: '/report/near-miss', icon: mdiHardHat },
    ],
  },
  {
    id: v4(),
    text: 'Resources',
    children: [
      { id: v4(), text: 'Search resources', link: '/resources', icon: mdiFileSearch },
      { id: v4(), text: 'Upload resource', link: '/resources/upload', icon: mdiUpload },
    ],
  },
]

export default menu
