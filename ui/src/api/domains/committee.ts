import type { ApiProps } from '@/api/types'

type CommitteeRoleBase = {
  name: string
  description: string
  email: string
}

export type CommitteeResponseRole = CommitteeRoleBase & {
  id: number
  user?: string
}
export type CommitteeResponse = {
  roles: CommitteeResponseRole[]
}

export type CommitteeRequestRole = CommitteeRoleBase & {
  id?: number
  userId?: string
}
export type UpdateCommitteeRequest = {
  roles: CommitteeRequestRole[]
}

export default function({ http, extractData }: ApiProps) {
  const basePath = '/committee'

  return {
    list: async(): Promise<CommitteeResponse> => http.get<CommitteeResponse>(basePath).then(extractData),
    update: async(request: UpdateCommitteeRequest) : Promise<void> => http.put(basePath, request),
  }
}
